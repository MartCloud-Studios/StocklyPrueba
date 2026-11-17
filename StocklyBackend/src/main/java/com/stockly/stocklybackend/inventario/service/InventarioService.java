package com.stockly.stocklybackend.inventario.service;

import com.stockly.stocklybackend.acceso.entity.Negocio;
import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.entity.Usuario;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.catalogo.dto.ProductoResponse;
import com.stockly.stocklybackend.catalogo.entity.Producto;
import com.stockly.stocklybackend.catalogo.service.CatalogoService;
import com.stockly.stocklybackend.inventario.dto.*;
import com.stockly.stocklybackend.inventario.entity.Inventario;
import com.stockly.stocklybackend.inventario.entity.MovimientoInventario;
import com.stockly.stocklybackend.inventario.entity.TipoMovimiento;
import com.stockly.stocklybackend.inventario.entity.TipoReferencia;
import com.stockly.stocklybackend.inventario.repository.InventarioRepository;
import com.stockly.stocklybackend.inventario.repository.MovimientoInventarioRepository;
import com.stockly.stocklybackend.inventario.repository.MovimientoSpecs;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private static final int ESCALA = 3;

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final NegocioUsuarioRepository negocioUsuarioRepository;
    private final CatalogoService catalogoService;
    private final EntityManager entityManager;

    // ================= Operaciones manuales (las usa el controlador) =================

    /** Primera entrada de un producto; solo si aún no tiene movimientos. */
    @Transactional
    public MovimientoResponse registrarStockInicial(Long negocioId, Long usuarioId,
                                                    EntradaRequest request) {
        exigirEscritura(negocioId, usuarioId);
        if (movimientoRepository.existsByNegocioIdAndProductoId(negocioId, request.productoId())) {
            throw new ResponseStatusException(CONFLICT,
                    "El producto ya tiene movimientos; para corregir el stock use un ajuste");
        }
        return aRespuesta(registrarEntrada(negocioId, request.productoId(),
                request.cantidad(), request.motivo(), usuarioId, null, null));
    }

    @Transactional
    public MovimientoResponse registrarEntradaManual(Long negocioId, Long usuarioId,
                                                     EntradaRequest request) {
        exigirEscritura(negocioId, usuarioId);
        return aRespuesta(registrarEntrada(negocioId, request.productoId(),
                request.cantidad(), request.motivo(), usuarioId, null, null));
    }

    /** Lleva el stock a la cantidad contada y guarda la diferencia con signo (real - actual). */
    @Transactional
    public MovimientoResponse registrarAjuste(Long negocioId, Long usuarioId,
                                              AjusteRequest request) {
        exigirEscritura(negocioId, usuarioId);
        Long productoId = request.productoId();
        catalogoService.obtenerProductoActivo(negocioId, productoId);
        if (request.cantidadReal().signum() > 0) {
            catalogoService.validarCantidad(negocioId, productoId, request.cantidadReal());
        }
        BigDecimal real = conEscala(request.cantidadReal());

        Inventario inventario = bloquearOCrear(negocioId, productoId);
        BigDecimal diferencia = real.subtract(inventario.getCantidad());
        if (diferencia.signum() == 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "La cantidad real es igual al stock actual; no hay nada que ajustar");
        }
        return aRespuesta(aplicar(inventario, TipoMovimiento.AJUSTE, diferencia,
                request.motivo(), usuarioId, null, null));
    }

    // ================= Consultas (las usa el controlador) =================

    /** Stock de los productos activos; los que no tienen movimientos salen en 0. */
    @Transactional(readOnly = true)
    public List<StockResponse> listarStock(Long negocioId, Long usuarioId, boolean soloBajos) {
        exigirLectura(negocioId, usuarioId);
        Map<Long, BigDecimal> stock = inventarioRepository.findByNegocioId(negocioId).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), Inventario::getCantidad));

        return catalogoService.listarProductos(negocioId, usuarioId, null, null, true).stream()
                .map(p -> aRespuesta(p, stock.getOrDefault(p.id(), BigDecimal.ZERO)))
                .filter(r -> !soloBajos || r.bajoMinimo())
                .toList();
    }

    @Transactional(readOnly = true)
    public StockResponse consultarStock(Long negocioId, Long usuarioId, Long productoId) {
        exigirLectura(negocioId, usuarioId);
        ProductoResponse producto = catalogoService.consultarProducto(negocioId, usuarioId, productoId);
        return aRespuesta(producto, obtenerStock(negocioId, productoId));
    }

    @Transactional(readOnly = true)
    public Page<MovimientoResponse> listarMovimientos(Long negocioId, Long usuarioId,
                                                      Long productoId, TipoMovimiento tipo,
                                                      LocalDateTime desde, LocalDateTime hasta,
                                                      Pageable pageable) {
        exigirLectura(negocioId, usuarioId);
        return movimientoRepository
                .findAll(MovimientoSpecs.filtrar(negocioId, productoId, tipo, desde, hasta), pageable)
                .map(MovimientoResponse::from);
    }

    // ================= API para otros módulos (compras y ventas) =================
    // No comprueban rol: quien llama ya validó los permisos. Corren en la transacción
    // de quien llama, así que si la venta o la compra falla, el movimiento se revierte.
    // La referencia (tipo + id) identifica la compra o venta que originó el movimiento:
    // se envían las dos o ninguna.

    @Transactional
    public MovimientoInventario registrarEntrada(Long negocioId, Long productoId,
                                                 BigDecimal cantidad, String motivo,
                                                 Long usuarioId, TipoReferencia referenciaTipo,
                                                 Long referenciaId) {
        catalogoService.validarCantidad(negocioId, productoId, cantidad);
        Inventario inventario = bloquearOCrear(negocioId, productoId);
        return aplicar(inventario, TipoMovimiento.ENTRADA, conEscala(cantidad),
                motivo, usuarioId, referenciaTipo, referenciaId);
    }

    /** Entrada de varios productos (una compra), todo o nada. */
    @Transactional
    public List<MovimientoInventario> registrarEntradas(Long negocioId, List<ItemMovimiento> items,
                                                        String motivo, Long usuarioId,
                                                        TipoReferencia referenciaTipo,
                                                        Long referenciaId) {
        List<MovimientoInventario> resultado = new ArrayList<>();
        agruparPorProducto(items).forEach((productoId, cantidad) ->
                resultado.add(registrarEntrada(negocioId, productoId, cantidad, motivo,
                        usuarioId, referenciaTipo, referenciaId)));
        return resultado;
    }

    @Transactional
    public MovimientoInventario registrarSalida(Long negocioId, Long productoId,
                                                BigDecimal cantidad, String motivo,
                                                Long usuarioId, TipoReferencia referenciaTipo,
                                                Long referenciaId) {
        catalogoService.validarCantidad(negocioId, productoId, cantidad);
        BigDecimal valida = conEscala(cantidad);
        Inventario inventario = inventarioRepository.buscarBloqueando(negocioId, productoId)
                .orElseThrow(() -> stockInsuficiente(productoId, BigDecimal.ZERO, valida));
        return aplicar(inventario, TipoMovimiento.SALIDA, valida,
                motivo, usuarioId, referenciaTipo, referenciaId);
    }

    /** Salida de varios productos (una venta), todo o nada. */
    @Transactional
    public List<MovimientoInventario> registrarSalidas(Long negocioId, List<ItemMovimiento> items,
                                                       String motivo, Long usuarioId,
                                                       TipoReferencia referenciaTipo,
                                                       Long referenciaId) {
        List<MovimientoInventario> resultado = new ArrayList<>();
        agruparPorProducto(items).forEach((productoId, cantidad) ->
                resultado.add(registrarSalida(negocioId, productoId, cantidad, motivo,
                        usuarioId, referenciaTipo, referenciaId)));
        return resultado;
    }

    /** Stock actual; 0 si el producto aún no tiene movimientos. */
    @Transactional(readOnly = true)
    public BigDecimal obtenerStock(Long negocioId, Long productoId) {
        return inventarioRepository.findByNegocioIdAndProductoId(negocioId, productoId)
                .map(Inventario::getCantidad)
                .orElse(BigDecimal.ZERO);
    }

    // ================= Reglas auxiliares =================

    private MovimientoInventario aplicar(Inventario inventario, TipoMovimiento tipo,
                                         BigDecimal cantidad, String motivo, Long usuarioId,
                                         TipoReferencia referenciaTipo, Long referenciaId) {
        if ((referenciaTipo == null) != (referenciaId == null)) {
            throw new IllegalArgumentException("La referencia requiere tipo e id, o ninguno");
        }
        BigDecimal anterior = inventario.getCantidad();
        BigDecimal delta = tipo == TipoMovimiento.SALIDA ? cantidad.negate() : cantidad;
        BigDecimal resultante = anterior.add(delta);
        if (resultante.signum() < 0) {
            throw stockInsuficiente(inventario.getProducto().getId(), anterior, cantidad.abs());
        }
        inventario.setCantidad(resultante);

        return movimientoRepository.save(MovimientoInventario.builder()
                .negocio(inventario.getNegocio())
                .producto(inventario.getProducto())
                .usuario(entityManager.getReference(Usuario.class, usuarioId))
                .tipo(tipo)
                .cantidad(cantidad)
                .stockAnterior(anterior)
                .stockResultante(resultante)
                .motivo(limpiarOpcional(motivo))
                .referenciaTipo(referenciaTipo)
                .referenciaId(referenciaId)
                .build());
    }

    /**
     * Suma los ítems repetidos y los ordena por productoId, para bloquear siempre en el
     * mismo orden y evitar deadlocks entre operaciones simultáneas.
     */
    private Map<Long, BigDecimal> agruparPorProducto(List<ItemMovimiento> items) {
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Debe indicar al menos un producto");
        }
        Map<Long, BigDecimal> totales = new TreeMap<>();
        for (ItemMovimiento item : items) {
            if (item == null || item.productoId() == null || item.cantidad() == null) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "Cada ítem requiere producto y cantidad");
            }
            totales.merge(item.productoId(), item.cantidad(), BigDecimal::add);
        }
        return totales;
    }

    /**
     * Fila de stock bloqueada; si el producto aún no tiene, la crea en 0.
     * Si dos primeros movimientos del mismo producto coinciden, el segundo falla por la
     * restricción única y debe reintentarse.
     */
    private Inventario bloquearOCrear(Long negocioId, Long productoId) {
        return inventarioRepository.buscarBloqueando(negocioId, productoId)
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setNegocio(entityManager.getReference(Negocio.class, negocioId));
                    nuevo.setProducto(entityManager.getReference(Producto.class, productoId));
                    return inventarioRepository.saveAndFlush(nuevo);
                });
    }

    /** Las cantidades ya pasaron por CatalogoService, así que nunca pierden decimales. */
    private BigDecimal conEscala(BigDecimal valor) {
        return valor.setScale(ESCALA, RoundingMode.UNNECESSARY);
    }

    private ResponseStatusException stockInsuficiente(Long productoId, BigDecimal disponible,
                                                      BigDecimal solicitado) {
        return new ResponseStatusException(CONFLICT,
                "Stock insuficiente para el producto " + productoId
                        + ": disponible " + disponible.toPlainString()
                        + ", solicitado " + solicitado.toPlainString());
    }

    private NegocioUsuario exigirLectura(Long negocioId, Long usuarioId) {
        return negocioUsuarioRepository.findByUsuarioIdAndNegocioId(usuarioId, negocioId)
                .filter(NegocioUsuario::isActivo)
                .filter(miembro -> miembro.getNegocio().isActivo())
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN,
                        "No perteneces a este negocio"));
    }

    private NegocioUsuario exigirEscritura(Long negocioId, Long usuarioId) {
        NegocioUsuario miembro = exigirLectura(negocioId, usuarioId);
        NombreRol rol = miembro.getRol().getNombre();
        if (rol != NombreRol.PROPIETARIO && rol != NombreRol.ALMACENISTA) {
            throw new ResponseStatusException(FORBIDDEN,
                    "No tienes permiso para modificar el inventario");
        }
        return miembro;
    }

    private String limpiarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    /** Bajo mínimo: el stock mínimo es mayor que 0 y el stock actual es menor o igual a él. */
    private StockResponse aRespuesta(ProductoResponse producto, BigDecimal cantidad) {
        BigDecimal minimo = producto.stockMinimo();
        boolean bajo = minimo.signum() > 0 && cantidad.compareTo(minimo) <= 0;
        return new StockResponse(producto.id(), producto.nombre(), producto.unidadMedidaNombre(),
                cantidad, minimo, bajo);
    }

    private MovimientoResponse aRespuesta(MovimientoInventario movimiento) {
        return MovimientoResponse.from(movimiento);
    }
}