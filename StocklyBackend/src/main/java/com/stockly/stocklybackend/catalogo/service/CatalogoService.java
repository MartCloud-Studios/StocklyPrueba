package com.stockly.stocklybackend.catalogo.service;

import com.stockly.stocklybackend.acceso.entity.NegocioUsuario;
import com.stockly.stocklybackend.acceso.entity.NombreRol;
import com.stockly.stocklybackend.acceso.repository.NegocioUsuarioRepository;
import com.stockly.stocklybackend.catalogo.dto.ActualizarProductoRequest;
import com.stockly.stocklybackend.catalogo.dto.CategoriaRequest;
import com.stockly.stocklybackend.catalogo.dto.CategoriaResponse;
import com.stockly.stocklybackend.catalogo.dto.CrearProductoRequest;
import com.stockly.stocklybackend.catalogo.dto.ProductoResponse;
import com.stockly.stocklybackend.catalogo.dto.ProveedorRequest;
import com.stockly.stocklybackend.catalogo.dto.ProveedorResponse;
import com.stockly.stocklybackend.catalogo.dto.UnidadMedidaRequest;
import com.stockly.stocklybackend.catalogo.dto.UnidadMedidaResponse;
import com.stockly.stocklybackend.catalogo.entity.Categoria;
import com.stockly.stocklybackend.catalogo.entity.Producto;
import com.stockly.stocklybackend.catalogo.entity.Proveedor;
import com.stockly.stocklybackend.catalogo.entity.UnidadMedida;
import com.stockly.stocklybackend.catalogo.repository.CategoriaRepository;
import com.stockly.stocklybackend.catalogo.repository.ProductoRepository;
import com.stockly.stocklybackend.catalogo.repository.ProductoSpecs;
import com.stockly.stocklybackend.catalogo.repository.ProveedorRepository;
import com.stockly.stocklybackend.catalogo.repository.UnidadMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CatalogoService {
    private static final String UNIDAD_BASE = "Unidad";

    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final NegocioUsuarioRepository negocioUsuarioRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias(Long negocioId, Long usuarioId) {
        exigirLectura(negocioId, usuarioId);
        return categoriaRepository.findByNegocioIdOrderByNombreAsc(negocioId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional
    public CategoriaResponse crearCategoria(Long negocioId, Long usuarioId,
                                             CategoriaRequest request) {
        NegocioUsuario miembro = exigirEscritura(negocioId, usuarioId);
        String nombre = limpiar(request.nombre());
        exigirNombreUnicoCategoria(negocioId, nombre, null);

        Categoria categoria = new Categoria();
        categoria.setNegocio(miembro.getNegocio());
        categoria.setNombre(nombre);
        return aRespuesta(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizarCategoria(Long negocioId, Long usuarioId,
                                                   Long categoriaId,
                                                   CategoriaRequest request) {
        exigirEscritura(negocioId, usuarioId);
        Categoria categoria = buscarCategoria(negocioId, categoriaId);
        String nombre = limpiar(request.nombre());
        exigirNombreUnicoCategoria(negocioId, nombre, categoriaId);
        categoria.setNombre(nombre);
        return aRespuesta(categoria);
    }

    @Transactional
    public CategoriaResponse desactivarCategoria(Long negocioId, Long usuarioId,
                                                  Long categoriaId) {
        exigirEscritura(negocioId, usuarioId);
        Categoria categoria = buscarCategoria(negocioId, categoriaId);
        categoria.setActivo(false);
        return aRespuesta(categoria);
    }

    @Transactional
    public CategoriaResponse reactivarCategoria(Long negocioId, Long usuarioId,
                                                 Long categoriaId) {
        exigirEscritura(negocioId, usuarioId);
        Categoria categoria = buscarCategoria(negocioId, categoriaId);
        categoria.setActivo(true);
        return aRespuesta(categoria);
    }

    @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> listarUnidades(Long negocioId, Long usuarioId) {
        exigirLectura(negocioId, usuarioId);
        return unidadMedidaRepository
                .findByNegocioIdOrNegocioIsNullOrderByNombreAsc(negocioId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional
    public UnidadMedidaResponse crearUnidad(Long negocioId, Long usuarioId,
                                             UnidadMedidaRequest request) {
        NegocioUsuario miembro = exigirEscritura(negocioId, usuarioId);
        String nombre = limpiar(request.nombre());
        if (nombre.equalsIgnoreCase(UNIDAD_BASE)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "El nombre Unidad está reservado para la medida base");
        }
        exigirNombreUnicoUnidad(negocioId, nombre, null);

        UnidadMedida unidad = new UnidadMedida();
        unidad.setNegocio(miembro.getNegocio());
        unidad.setNombre(nombre);
        return aRespuesta(unidadMedidaRepository.save(unidad));
    }

    @Transactional
    public UnidadMedidaResponse renombrarUnidad(Long negocioId, Long usuarioId,
                                                 Long unidadId,
                                                 UnidadMedidaRequest request) {
        exigirEscritura(negocioId, usuarioId);
        UnidadMedida unidad = buscarUnidadDelNegocio(negocioId, unidadId);
        String nombre = limpiar(request.nombre());
        if (unidad.isBase()) {
            throw new ResponseStatusException(FORBIDDEN,
                    "La medida base no se puede editar");
        }
        if (nombre.equalsIgnoreCase(UNIDAD_BASE)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "El nombre Unidad está reservado para la medida base");
        }
        exigirNombreUnicoUnidad(negocioId, nombre, unidadId);
        unidad.setNombre(nombre);
        return aRespuesta(unidad);
    }

    @Transactional
    public UnidadMedidaResponse desactivarUnidad(Long negocioId, Long usuarioId,
                                                  Long unidadId) {
        exigirEscritura(negocioId, usuarioId);
        UnidadMedida unidad = buscarUnidadDelNegocio(negocioId, unidadId);
        if (unidad.isBase()) {
            throw new ResponseStatusException(FORBIDDEN,
                    "La medida base no se puede desactivar");
        }
        unidad.setActivo(false);
        return aRespuesta(unidad);
    }

    @Transactional
    public UnidadMedidaResponse reactivarUnidad(Long negocioId, Long usuarioId,
                                                 Long unidadId) {
        exigirEscritura(negocioId, usuarioId);
        UnidadMedida unidad = buscarUnidadDelNegocio(negocioId, unidadId);
        if (unidad.isBase()) {
            throw new ResponseStatusException(FORBIDDEN,
                    "La medida base no se puede editar");
        }
        unidad.setActivo(true);
        return aRespuesta(unidad);
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> listarProveedores(Long negocioId, Long usuarioId) {
        exigirLectura(negocioId, usuarioId);
        return proveedorRepository.findByNegocioIdOrderByNombreAsc(negocioId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional
    public ProveedorResponse crearProveedor(Long negocioId, Long usuarioId,
                                            ProveedorRequest request) {
        NegocioUsuario miembro = exigirEscritura(negocioId, usuarioId);
        String nombre = limpiar(request.nombre());
        exigirNombreUnicoProveedor(negocioId, nombre, null);

        Proveedor proveedor = new Proveedor();
        proveedor.setNegocio(miembro.getNegocio());
        proveedor.setNombre(nombre);
        proveedor.setTelefono(limpiarOpcional(request.telefono()));
        proveedor.setEmail(limpiarOpcional(request.email()));
        return aRespuesta(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorResponse actualizarProveedor(Long negocioId, Long usuarioId,
                                                 Long proveedorId,
                                                 ProveedorRequest request) {
        exigirEscritura(negocioId, usuarioId);
        Proveedor proveedor = buscarProveedor(negocioId, proveedorId);
        String nombre = limpiar(request.nombre());
        exigirNombreUnicoProveedor(negocioId, nombre, proveedorId);

        proveedor.setNombre(nombre);
        proveedor.setTelefono(limpiarOpcional(request.telefono()));
        proveedor.setEmail(limpiarOpcional(request.email()));
        return aRespuesta(proveedor);
    }

    @Transactional
    public ProveedorResponse desactivarProveedor(Long negocioId, Long usuarioId,
                                                 Long proveedorId) {
        exigirEscritura(negocioId, usuarioId);
        Proveedor proveedor = buscarProveedor(negocioId, proveedorId);
        proveedor.setActivo(false);
        return aRespuesta(proveedor);
    }

    @Transactional
    public ProveedorResponse reactivarProveedor(Long negocioId, Long usuarioId,
                                                Long proveedorId) {
        exigirEscritura(negocioId, usuarioId);
        Proveedor proveedor = buscarProveedor(negocioId, proveedorId);
        proveedor.setActivo(true);
        return aRespuesta(proveedor);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos(Long negocioId, Long usuarioId) {
        return listarProductos(negocioId, usuarioId, null, null, false);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos(Long negocioId, Long usuarioId,
                                                  Long proveedorId, Long categoriaId,
                                                  boolean soloActivos) {
        exigirLectura(negocioId, usuarioId);
        return productoRepository
                .findAll(ProductoSpecs.filtrar(negocioId, proveedorId, categoriaId, soloActivos),
                        Sort.by(Sort.Direction.ASC, "nombre"))
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse consultarProducto(Long negocioId, Long usuarioId,
                                               Long productoId) {
        exigirLectura(negocioId, usuarioId);
        return aRespuesta(buscarProducto(negocioId, productoId));
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerProductoActivo(Long negocioId, Long productoId) {
        return aRespuesta(buscarProductoActivo(negocioId, productoId));
    }

    @Transactional(readOnly = true)
    public ProveedorResponse obtenerProveedorActivo(Long negocioId, Long proveedorId) {
        return aRespuesta(buscarProveedorActivo(negocioId, proveedorId));
    }

    @Transactional
    public ProductoResponse crearProducto(Long negocioId, Long usuarioId,
                                           CrearProductoRequest request) {
        NegocioUsuario miembro = exigirEscritura(negocioId, usuarioId);
        validarPreciosYStock(request.precioCosto(), request.precioVenta(),
                request.stockMinimo());
        UnidadMedida unidad = request.unidadMedidaId() == null
                ? buscarUnidadBase()
                : buscarUnidadAsignable(negocioId, request.unidadMedidaId());
        validarStockMinimo(unidad, request.stockMinimo());
        Categoria categoria = request.categoriaId() == null
                ? null
                : buscarCategoriaActiva(negocioId, request.categoriaId());
        Proveedor proveedor = request.proveedorId() == null
                ? null
                : buscarProveedorActivo(negocioId, request.proveedorId());
        String codigoBarras = limpiarOpcional(request.codigoBarras());
        exigirCodigoBarrasUnico(negocioId, codigoBarras, null);

        Producto producto = new Producto();
        producto.setNegocio(miembro.getNegocio());
        producto.setUnidadMedida(unidad);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        aplicarDatosProducto(producto, codigoBarras, request.nombre(),
                request.descripcion(), request.precioCosto(), request.precioVenta(),
                request.stockMinimo());
        return aRespuesta(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizarProducto(Long negocioId, Long usuarioId,
                                                Long productoId,
                                                ActualizarProductoRequest request) {
        exigirEscritura(negocioId, usuarioId);
        Producto producto = buscarProducto(negocioId, productoId);
        validarPreciosYStock(request.precioCosto(), request.precioVenta(),
                request.stockMinimo());
        validarStockMinimo(producto.getUnidadMedida(), request.stockMinimo());

        Categoria categoria = null;
        if (request.categoriaId() != null) {
            boolean conservaCategoria = producto.getCategoria() != null
                    && producto.getCategoria().getId().equals(request.categoriaId());
            categoria = conservaCategoria
                    ? buscarCategoria(negocioId, request.categoriaId())
                    : buscarCategoriaActiva(negocioId, request.categoriaId());
        }
        Proveedor proveedor = null;
        if (request.proveedorId() != null) {
            boolean conservaProveedor = producto.getProveedor() != null
                    && producto.getProveedor().getId().equals(request.proveedorId());
            proveedor = conservaProveedor
                    ? buscarProveedor(negocioId, request.proveedorId())
                    : buscarProveedorActivo(negocioId, request.proveedorId());
        }
        String codigoBarras = limpiarOpcional(request.codigoBarras());
        exigirCodigoBarrasUnico(negocioId, codigoBarras, productoId);

        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        aplicarDatosProducto(producto, codigoBarras, request.nombre(),
                request.descripcion(), request.precioCosto(), request.precioVenta(),
                request.stockMinimo());
        return aRespuesta(producto);
    }

    @Transactional
    public ProductoResponse desactivarProducto(Long negocioId, Long usuarioId,
                                                Long productoId) {
        exigirEscritura(negocioId, usuarioId);
        Producto producto = buscarProducto(negocioId, productoId);
        producto.setActivo(false);
        return aRespuesta(producto);
    }

    @Transactional
    public ProductoResponse reactivarProducto(Long negocioId, Long usuarioId,
                                               Long productoId) {
        exigirEscritura(negocioId, usuarioId);
        Producto producto = buscarProducto(negocioId, productoId);
        producto.setActivo(true);
        return aRespuesta(producto);
    }

    /**
     * Entry point for inventory, sales and purchasing to validate a requested quantity.
     */
    @Transactional(readOnly = true)
    public void validarCantidad(Long negocioId, Long productoId, BigDecimal cantidad) {
        Producto producto = buscarProductoActivo(negocioId, productoId);
        validarCantidad(producto.getUnidadMedida(), cantidad);
    }

    /**
     * Entry point for purchasing: sets the product cost to the cost of the last purchase.
     * It does not check roles; the caller already validated permissions.
     */
    @Transactional
    public void actualizarPrecioCosto(Long negocioId, Long productoId, BigDecimal precioCosto) {
        if (precioCosto == null || precioCosto.signum() < 0
                || precioCosto.stripTrailingZeros().scale() > 2) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "El precio de costo debe ser mayor o igual a cero y admite 2 decimales");
        }
        Producto producto = buscarProducto(negocioId, productoId);
        producto.setPrecioCosto(precioCosto.setScale(2, RoundingMode.UNNECESSARY));
    }

    private void validarCantidad(UnidadMedida unidad, BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "La cantidad debe ser mayor que cero");
        }
        int decimales = Math.max(0, cantidad.stripTrailingZeros().scale());
        if (unidad.isBase() && decimales > 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "La medida Unidad solo acepta cantidades enteras");
        }
        if (!unidad.isBase() && decimales > 3) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "La cantidad admite como máximo 3 decimales");
        }
    }

    private void validarPreciosYStock(BigDecimal costo, BigDecimal venta,
                                      BigDecimal stockMinimo) {
        if (costo == null || venta == null || stockMinimo == null
                || costo.signum() < 0 || venta.signum() < 0
                || stockMinimo.signum() < 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Los precios y el stock mínimo deben ser mayores o iguales a cero");
        }
        if (costo.scale() > 2 || venta.scale() > 2 || stockMinimo.scale() > 3) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Los precios admiten 2 decimales y el stock mínimo 3");
        }
    }

    private void validarStockMinimo(UnidadMedida unidad, BigDecimal stockMinimo) {
        if (unidad.isBase()
                && Math.max(0, stockMinimo.stripTrailingZeros().scale()) > 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "El stock mínimo debe ser entero cuando la medida es Unidad");
        }
    }

    private UnidadMedida buscarUnidadBase() {
        return unidadMedidaRepository
                .findByNegocioIsNullAndNombreIgnoreCase(UNIDAD_BASE)
                .filter(UnidadMedida::isActivo)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe la medida base Unidad"));
    }

    private UnidadMedida buscarUnidadAsignable(Long negocioId, Long unidadId) {
        UnidadMedida unidad = unidadMedidaRepository.findById(unidadId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST,
                        "La medida no pertenece a este negocio"));
        if (!unidad.isBase() && !unidad.getNegocio().getId().equals(negocioId)) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "La medida no pertenece a este negocio");
        }
        if (!unidad.isActivo()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No se puede asignar una medida desactivada");
        }
        return unidad;
    }

    private UnidadMedida buscarUnidadDelNegocio(Long negocioId, Long unidadId) {
        return unidadMedidaRepository.findById(unidadId)
                .filter(unidad -> unidad.isBase()
                        || unidad.getNegocio().getId().equals(negocioId))
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Medida no encontrada"));
    }

    private Categoria buscarCategoria(Long negocioId, Long categoriaId) {
        return categoriaRepository.findByIdAndNegocioId(categoriaId, negocioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Categoría no encontrada"));
    }

    private Categoria buscarCategoriaActiva(Long negocioId, Long categoriaId) {
        Categoria categoria = buscarCategoria(negocioId, categoriaId);
        if (!categoria.isActivo()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No se puede asignar una categoría desactivada");
        }
        return categoria;
    }

    private Proveedor buscarProveedor(Long negocioId, Long proveedorId) {
        return proveedorRepository.findByIdAndNegocioId(proveedorId, negocioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Proveedor no encontrado"));
    }

    private Proveedor buscarProveedorActivo(Long negocioId, Long proveedorId) {
        Proveedor proveedor = buscarProveedor(negocioId, proveedorId);
        if (!proveedor.isActivo()) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "No se puede asignar un proveedor desactivado");
        }
        return proveedor;
    }

    private Producto buscarProducto(Long negocioId, Long productoId) {
        return productoRepository.findByIdAndNegocioId(productoId, negocioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Producto no encontrado"));
    }

    private Producto buscarProductoActivo(Long negocioId, Long productoId) {
        return productoRepository.findByIdAndNegocioIdAndActivoTrue(productoId, negocioId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Producto activo no encontrado"));
    }

    private void exigirNombreUnicoCategoria(Long negocioId, String nombre, Long id) {
        boolean existe = id == null
                ? categoriaRepository.existsByNegocioIdAndNombreIgnoreCase(negocioId, nombre)
                : categoriaRepository.existsByNegocioIdAndNombreIgnoreCaseAndIdNot(
                        negocioId, nombre, id);
        if (existe) {
            throw new ResponseStatusException(CONFLICT,
                    "Ya existe una categoría con ese nombre en el negocio");
        }
    }

    private void exigirNombreUnicoUnidad(Long negocioId, String nombre, Long id) {
        boolean existe = id == null
                ? unidadMedidaRepository.existsByNegocioIdAndNombreIgnoreCase(negocioId, nombre)
                : unidadMedidaRepository.existsByNegocioIdAndNombreIgnoreCaseAndIdNot(
                        negocioId, nombre, id);
        if (existe) {
            throw new ResponseStatusException(CONFLICT,
                    "Ya existe una medida con ese nombre en el negocio");
        }
    }

    private void exigirNombreUnicoProveedor(Long negocioId, String nombre, Long id) {
        boolean existe = id == null
                ? proveedorRepository.existsByNegocioIdAndNombreIgnoreCase(negocioId, nombre)
                : proveedorRepository.existsByNegocioIdAndNombreIgnoreCaseAndIdNot(
                        negocioId, nombre, id);
        if (existe) {
            throw new ResponseStatusException(CONFLICT,
                    "Ya existe un proveedor con ese nombre en el negocio");
        }
    }

    private void exigirCodigoBarrasUnico(Long negocioId, String codigoBarras, Long id) {
        if (codigoBarras == null) {
            return;
        }
        boolean existe = id == null
                ? productoRepository.existsByNegocioIdAndCodigoBarras(negocioId, codigoBarras)
                : productoRepository.existsByNegocioIdAndCodigoBarrasAndIdNot(
                        negocioId, codigoBarras, id);
        if (existe) {
            throw new ResponseStatusException(CONFLICT,
                    "Ya existe un producto con ese código de barras en el negocio");
        }
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
                    "No tienes permiso para modificar el catálogo");
        }
        return miembro;
    }

    private void aplicarDatosProducto(Producto producto, String codigoBarras,
                                      String nombre, String descripcion,
                                      BigDecimal precioCosto, BigDecimal precioVenta,
                                      BigDecimal stockMinimo) {
        producto.setCodigoBarras(codigoBarras);
        producto.setNombre(limpiar(nombre));
        producto.setDescripcion(limpiarOpcional(descripcion));
        producto.setPrecioCosto(precioCosto);
        producto.setPrecioVenta(precioVenta);
        producto.setStockMinimo(stockMinimo);
    }

    private String limpiar(String valor) {
        return valor.trim();
    }

    private String limpiarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private CategoriaResponse aRespuesta(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNegocio().getId(),
                categoria.getNombre(), categoria.isActivo(), categoria.getCreatedAt());
    }

    private UnidadMedidaResponse aRespuesta(UnidadMedida unidad) {
        return new UnidadMedidaResponse(unidad.getId(),
                unidad.isBase() ? null : unidad.getNegocio().getId(),
                unidad.getNombre(), unidad.isBase(), unidad.admiteDecimales(),
                unidad.isActivo(), unidad.getCreatedAt());
    }

    private ProveedorResponse aRespuesta(Proveedor proveedor) {
        return new ProveedorResponse(proveedor.getId(), proveedor.getNegocio().getId(),
                proveedor.getNombre(), proveedor.getTelefono(), proveedor.getEmail(),
                proveedor.isActivo(), proveedor.getCreatedAt());
    }

    private ProductoResponse aRespuesta(Producto producto) {
        Categoria categoria = producto.getCategoria();
        Proveedor proveedor = producto.getProveedor();
        UnidadMedida unidad = producto.getUnidadMedida();
        return new ProductoResponse(producto.getId(), producto.getNegocio().getId(),
                categoria == null ? null : categoria.getId(),
                categoria == null ? null : categoria.getNombre(),
                proveedor == null ? null : proveedor.getId(),
                proveedor == null ? null : proveedor.getNombre(),
                unidad.getId(), unidad.getNombre(), unidad.isBase(),
                producto.getCodigoBarras(), producto.getNombre(), producto.getDescripcion(),
                producto.getPrecioCosto(), producto.getPrecioVenta(),
                producto.getStockMinimo(), producto.isActivo(),
                producto.getCreatedAt(), producto.getUpdatedAt());
    }
}
