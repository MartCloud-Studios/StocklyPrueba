package com.stockly.stocklybackend.ventas.service;

import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.catalogo.dto.ProductoResponse;
import com.stockly.stocklybackend.catalogo.entity.Producto;
import com.stockly.stocklybackend.catalogo.service.CatalogoService;
import com.stockly.stocklybackend.inventario.dto.ItemMovimiento;
import com.stockly.stocklybackend.inventario.entity.TipoReferencia;
import com.stockly.stocklybackend.inventario.service.InventarioService;
import com.stockly.stocklybackend.ventas.dto.*;
import com.stockly.stocklybackend.ventas.entity.DetalleVenta;
import com.stockly.stocklybackend.ventas.entity.Venta;
import com.stockly.stocklybackend.ventas.repository.VentaRepository;
import com.stockly.stocklybackend.ventas.repository.VentaSpecs;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final NegocioUsuarioRepository negocioUsuarioRepository;
    private final CatalogoService catalogoService;
    private final InventarioService inventarioService;
    private final EntityManager entityManager;

    /**
     * Confirma la venta: guarda la venta y su detalle con el precio y el costo del momento,
     * y descuenta el stock en inventario. Todo en una transacción: si falta stock en cualquier
     * producto, se rechaza toda la venta y no se guarda nada.
     */
    @Transactional
    public VentaResponse registrar(Long negocioId, Long usuarioId, CrearVentaRequest request) {
        NegocioUsuario miembro = exigirAcceso(negocioId, usuarioId);

        Venta venta = new Venta();
        venta.setNegocio(miembro.getNegocio());
        venta.setUsuario(miembro.getUsuario());
        venta.setMetodoPago(request.metodoPago());

        Set<Long> productosVistos = new HashSet<>();
        List<DetalleVentaResponse> lineas = new ArrayList<>();
        List<ItemMovimiento> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (LineaVentaRequest linea : request.lineas()) {
            if (!productosVistos.add(linea.productoId())) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "Un producto no puede repetirse en la misma venta");
            }
            // Producto del negocio y activo; de aquí salen el precio y el costo del momento
            ProductoResponse producto =
                    catalogoService.obtenerProductoActivo(negocioId, linea.productoId());
            catalogoService.validarCantidad(negocioId, linea.productoId(), linea.cantidad());
            if (producto.precioVenta().signum() <= 0) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "El producto " + producto.nombre() + " no tiene precio de venta");
            }

            BigDecimal cantidad = linea.cantidad().setScale(3, RoundingMode.UNNECESSARY);
            BigDecimal precioVenta = producto.precioVenta().setScale(2, RoundingMode.HALF_UP);
            BigDecimal precioCosto = producto.precioCosto().setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = cantidad.multiply(precioVenta).setScale(2, RoundingMode.HALF_UP);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(entityManager.getReference(Producto.class, producto.id()));
            detalle.setCantidad(cantidad);
            detalle.setPrecioVenta(precioVenta);
            detalle.setPrecioCosto(precioCosto);
            detalle.setSubtotal(subtotal);
            venta.getDetalles().add(detalle);

            lineas.add(new DetalleVentaResponse(producto.id(), producto.nombre(), cantidad,
                    precioVenta, precioCosto, subtotal));
            items.add(new ItemMovimiento(producto.id(), cantidad));
            total = total.add(subtotal);
        }
        if (total.signum() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "El total de la venta debe ser mayor que cero");
        }
        venta.setTotal(total);
        ventaRepository.save(venta);

        // Stock: una SALIDA por línea, con referencia a esta venta (todo o nada)
        inventarioService.registrarSalidas(negocioId, items, null, usuarioId,
                TipoReferencia.VENTA, venta.getId());

        return aRespuesta(venta, lineas);
    }

    /** PROPIETARIO y ALMACENISTA ven todas las ventas; el VENDEDOR solo las suyas. */
    @Transactional(readOnly = true)
    public Page<VentaResumenResponse> listar(Long negocioId, Long usuarioId, Long vendedorId,
                                             LocalDateTime desde, LocalDateTime hasta,
                                             Pageable pageable) {
        NegocioUsuario miembro = exigirAcceso(negocioId, usuarioId);
        Long filtroUsuario = esVendedor(miembro) ? usuarioId : vendedorId;
        return ventaRepository
                .findAll(VentaSpecs.filtrar(negocioId, filtroUsuario, desde, hasta), pageable)
                .map(this::aResumen);
    }

    @Transactional(readOnly = true)
    public VentaResponse consultar(Long negocioId, Long usuarioId, Long ventaId) {
        NegocioUsuario miembro = exigirAcceso(negocioId, usuarioId);
        Venta venta = ventaRepository.findWithDetallesByIdAndNegocioId(ventaId, negocioId)
                .filter(v -> !esVendedor(miembro) || v.getUsuario().getId().equals(usuarioId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Venta no encontrada"));
        return aRespuesta(venta);
    }

    // ---------- Reglas auxiliares ----------

    /** Cualquier miembro activo (de un negocio activo) puede vender y consultar. */
    private NegocioUsuario exigirAcceso(Long negocioId, Long usuarioId) {
        return negocioUsuarioRepository.findByUsuarioIdAndNegocioId(usuarioId, negocioId)
                .filter(NegocioUsuario::isActivo)
                .filter(miembro -> miembro.getNegocio().isActivo())
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN,
                        "No perteneces a este negocio"));
    }

    private boolean esVendedor(NegocioUsuario miembro) {
        return miembro.getRol().getNombre() == NombreRol.VENDEDOR;
    }

    private VentaResponse aRespuesta(Venta venta) {
        List<DetalleVentaResponse> lineas = venta.getDetalles().stream()
                .sorted(Comparator.comparing(DetalleVenta::getId))
                .map(d -> new DetalleVentaResponse(
                        d.getProducto().getId(), d.getProducto().getNombre(),
                        d.getCantidad(), d.getPrecioVenta(), d.getPrecioCosto(),
                        d.getSubtotal()))
                .toList();
        return aRespuesta(venta, lineas);
    }

    private VentaResponse aRespuesta(Venta venta, List<DetalleVentaResponse> lineas) {
        return new VentaResponse(venta.getId(), venta.getNegocio().getId(),
                venta.getUsuario().getId(), venta.getUsuario().getNombre(),
                venta.getMetodoPago(), venta.getTotal(), venta.getEstado(),
                venta.getCreatedAt(), lineas);
    }

    private VentaResumenResponse aResumen(Venta venta) {
        return new VentaResumenResponse(venta.getId(), venta.getUsuario().getId(),
                venta.getUsuario().getNombre(), venta.getMetodoPago(), venta.getTotal(),
                venta.getEstado(), venta.getCreatedAt());
    }
}