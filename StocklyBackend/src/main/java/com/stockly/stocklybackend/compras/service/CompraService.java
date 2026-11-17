package com.stockly.stocklybackend.compras.service;

import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.catalogo.entity.Producto;
import com.stockly.stocklybackend.catalogo.entity.Proveedor;
import com.stockly.stocklybackend.catalogo.service.CatalogoService;
import com.stockly.stocklybackend.compras.dto.*;
import com.stockly.stocklybackend.compras.entity.Compra;
import com.stockly.stocklybackend.compras.entity.DetalleCompra;
import com.stockly.stocklybackend.compras.repository.CompraRepository;
import com.stockly.stocklybackend.compras.repository.CompraSpecs;
import com.stockly.stocklybackend.compras.repository.DetalleCompraRepository;
import com.stockly.stocklybackend.inventario.dto.ItemMovimiento;
import com.stockly.stocklybackend.inventario.entity.TipoReferencia;
import com.stockly.stocklybackend.inventario.service.InventarioService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final NegocioUsuarioRepository negocioUsuarioRepository;
    private final CatalogoService catalogoService;
    private final InventarioService inventarioService;
    private final EntityManager entityManager;

    /**
     * Recibe la mercancía: guarda la compra y su detalle, suma el stock en inventario y
     * actualiza el costo de cada producto. Todo en una transacción: si algo falla, no se guarda nada.
     */
    @Transactional
    public CompraResponse registrar(Long negocioId, Long usuarioId, CrearCompraRequest request) {
        NegocioUsuario miembro = exigirAcceso(negocioId, usuarioId);

        Compra compra = new Compra();
        compra.setNegocio(miembro.getNegocio());
        compra.setUsuario(miembro.getUsuario());
        compra.setNumeroFactura(limpiarOpcional(request.numeroFactura()));
        if (request.proveedorId() != null) {
            catalogoService.obtenerProveedorActivo(negocioId, request.proveedorId());
            compra.setProveedor(entityManager.getReference(Proveedor.class, request.proveedorId()));
        }

        Set<Long> productosVistos = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        for (LineaCompraRequest linea : request.lineas()) {
            if (!productosVistos.add(linea.productoId())) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "Un producto no puede repetirse en la misma compra");
            }
            // Producto del negocio y activo, y cantidad válida según su medida
            catalogoService.obtenerProductoActivo(negocioId, linea.productoId());
            catalogoService.validarCantidad(negocioId, linea.productoId(), linea.cantidad());

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compra);
            detalle.setProducto(entityManager.getReference(Producto.class, linea.productoId()));
            detalle.setCantidad(linea.cantidad().setScale(3, RoundingMode.UNNECESSARY));
            detalle.setPrecioCosto(linea.precioCosto().setScale(2, RoundingMode.UNNECESSARY));
            detalle.setSubtotal(detalle.getCantidad().multiply(detalle.getPrecioCosto())
                    .setScale(2, RoundingMode.HALF_UP));
            compra.getDetalles().add(detalle);
            total = total.add(detalle.getSubtotal());
        }
        compra.setTotal(total);
        compraRepository.save(compra);

        // Stock: una ENTRADA por línea, con referencia a esta compra
        List<ItemMovimiento> items = compra.getDetalles().stream()
                .map(d -> new ItemMovimiento(d.getProducto().getId(), d.getCantidad()))
                .toList();
        inventarioService.registrarEntradas(negocioId, items, null, usuarioId,
                TipoReferencia.COMPRA, compra.getId());

        // Último costo de cada producto (en orden de id para evitar deadlocks entre compras simultáneas)
        compra.getDetalles().stream()
                .sorted(Comparator.comparing(d -> d.getProducto().getId()))
                .forEach(d -> catalogoService.actualizarPrecioCosto(
                        negocioId, d.getProducto().getId(), d.getPrecioCosto()));

        return aRespuesta(compra);
    }

    @Transactional(readOnly = true)
    public Page<CompraResumenResponse> listar(Long negocioId, Long usuarioId, Long proveedorId,
                                              LocalDateTime desde, LocalDateTime hasta,
                                              Pageable pageable) {
        exigirAcceso(negocioId, usuarioId);
        return compraRepository
                .findAll(CompraSpecs.filtrar(negocioId, proveedorId, desde, hasta), pageable)
                .map(this::aResumen);
    }

    @Transactional(readOnly = true)
    public CompraResponse consultar(Long negocioId, Long usuarioId, Long compraId) {
        exigirAcceso(negocioId, usuarioId);
        Compra compra = compraRepository.findWithDetallesByIdAndNegocioId(compraId, negocioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Compra no encontrada"));
        return aRespuesta(compra);
    }

    /** Lo que ha costado un producto en cada compra, de la más reciente a la más antigua. */
    @Transactional(readOnly = true)
    public Page<HistorialCostoResponse> historialCostos(Long negocioId, Long usuarioId,
                                                        Long productoId, Pageable pageable) {
        exigirAcceso(negocioId, usuarioId);
        catalogoService.consultarProducto(negocioId, usuarioId, productoId); // 404 si no existe
        return detalleCompraRepository
                .findByCompraNegocioIdAndProductoIdOrderByCompraCreatedAtDesc(
                        negocioId, productoId, pageable)
                .map(d -> new HistorialCostoResponse(
                        d.getCompra().getId(),
                        d.getCompra().getCreatedAt(),
                        d.getCompra().getProveedor() == null ? null : d.getCompra().getProveedor().getId(),
                        d.getCompra().getProveedor() == null ? null : d.getCompra().getProveedor().getNombre(),
                        d.getCantidad(),
                        d.getPrecioCosto()));
    }

    // ---------- Reglas auxiliares ----------

    /** Todo el módulo es solo para PROPIETARIO y ALMACENISTA activos de un negocio activo. */
    private NegocioUsuario exigirAcceso(Long negocioId, Long usuarioId) {
        NegocioUsuario miembro = negocioUsuarioRepository
                .findByUsuarioIdAndNegocioId(usuarioId, negocioId)
                .filter(NegocioUsuario::isActivo)
                .filter(m -> m.getNegocio().isActivo())
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN,
                        "No perteneces a este negocio"));
        NombreRol rol = miembro.getRol().getNombre();
        if (rol != NombreRol.PROPIETARIO && rol != NombreRol.ALMACENISTA) {
            throw new ResponseStatusException(FORBIDDEN,
                    "No tienes permiso para acceder a las compras");
        }
        return miembro;
    }

    private String limpiarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private CompraResponse aRespuesta(Compra compra) {
        Proveedor proveedor = compra.getProveedor();
        List<DetalleCompraResponse> lineas = compra.getDetalles().stream()
                .sorted(Comparator.comparing(DetalleCompra::getId))
                .map(d -> new DetalleCompraResponse(
                        d.getProducto().getId(), d.getProducto().getNombre(),
                        d.getCantidad(), d.getPrecioCosto(), d.getSubtotal()))
                .toList();
        return new CompraResponse(compra.getId(), compra.getNegocio().getId(),
                proveedor == null ? null : proveedor.getId(),
                proveedor == null ? null : proveedor.getNombre(),
                compra.getUsuario().getId(), compra.getUsuario().getNombre(),
                compra.getNumeroFactura(), compra.getTotal(), compra.getEstado(),
                compra.getCreatedAt(), lineas);
    }

    private CompraResumenResponse aResumen(Compra compra) {
        Proveedor proveedor = compra.getProveedor();
        return new CompraResumenResponse(compra.getId(),
                proveedor == null ? null : proveedor.getId(),
                proveedor == null ? null : proveedor.getNombre(),
                compra.getUsuario().getId(), compra.getUsuario().getNombre(),
                compra.getNumeroFactura(), compra.getTotal(), compra.getEstado(),
                compra.getCreatedAt());
    }
}