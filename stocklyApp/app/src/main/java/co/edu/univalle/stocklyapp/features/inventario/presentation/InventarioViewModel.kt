package co.edu.univalle.stocklyapp.features.inventario.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.univalle.stocklyapp.app.di.AppContainer
import co.edu.univalle.stocklyapp.core.network.safeApiCall
import co.edu.univalle.stocklyapp.core.session.PermisosNegocio
import co.edu.univalle.stocklyapp.core.session.SesionManager
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.ActualizarProductoRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.CategoriaRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.CrearProductoRequestDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.AjusteRequestDto
import co.edu.univalle.stocklyapp.features.inventario.data.dto.EntradaRequestDto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Etiqueta
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.UnidadMedida
import co.edu.univalle.stocklyapp.features.compras.domain.model.Proveedor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventarioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InventarioUiState())
    val uiState: StateFlow<InventarioUiState> = _uiState.asStateFlow()
    private val movimientosApi = AppContainer.inventarioApi
    private val catalogoApi = AppContainer.catalogoApi

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        val usuario = SesionManager.usuario.value
        val negocio = usuario?.negocioActual
        if (usuario == null || negocio == null) {
            _uiState.update { it.copy(sinSesion = true, cargando = false) }
            return
        }

        _uiState.update {
            it.copy(
                nombreNegocio = negocio.nombre,
                rolActual = negocio.rol,
                cargando = true,
                sinSesion = false
            )
        }

        viewModelScope.launch {
            when (val result = safeApiCall {
                val categorias = catalogoApi.categorias(negocio.negocioId)
                val unidades = catalogoApi.unidades(negocio.negocioId)
                val proveedores = catalogoApi.proveedores(negocio.negocioId)
                val productos = catalogoApi.productos(negocio.negocioId, soloActivos = false)
                val stock = movimientosApi.stock(negocio.negocioId)
                InventarioData(categorias, unidades, proveedores, productos, stock)
            }) {
                is Resource.Success -> {
                    val data = result.data
                    val stockPorProducto = data.stock.associateBy { it.productoId }
                    val categorias = data.categorias.map {
                        Etiqueta(it.id, it.nombre, activo = it.activo)
                    }
                    val unidades = data.unidades.filter { it.activo }.map {
                        UnidadMedida(
                            id = it.id,
                            nombre = it.nombre,
                            abreviatura = it.nombre,
                            esPersonalizada = !it.base,
                            admiteDecimales = it.admiteDecimales
                        )
                    }
                    val productos = data.productos.map { dto ->
                        val existencia = stockPorProducto[dto.id]
                        val categoria = categorias.firstOrNull { it.id == dto.categoriaId }
                        val unidad = unidades.firstOrNull { it.id == dto.unidadMedidaId }
                            ?: UnidadMedida(dto.unidadMedidaId, dto.unidadMedidaNombre, dto.unidadMedidaNombre)
                        Producto(
                            id = dto.id,
                            negocioId = dto.negocioId,
                            nombre = dto.nombre,
                            codigoBarras = dto.codigoBarras,
                            imagenUrl = null,
                            etiqueta = categoria,
                            unidadMedida = unidad,
                            cantidadDisponible = (existencia?.cantidad ?: 0.0).toFloat(),
                            cantidadMinima = dto.stockMinimo.toFloat(),
                            costoActual = dto.precioCosto,
                            precioVenta = dto.precioVenta,
                            fechaIngreso = dto.createdAt,
                            descripcion = dto.descripcion,
                            proveedorId = dto.proveedorId,
                            activo = dto.activo
                        )
                    }
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            etiquetas = categorias,
                            unidadesMedida = unidades,
                            proveedores = data.proveedores.map {
                                Proveedor(it.id, negocio.negocioId, it.nombre, it.telefono, it.email, activo = it.activo)
                            },
                            productos = productos,
                        )
                    }
                }
                is Resource.Error -> _uiState.update {
                    it.copy(cargando = false, mensaje = result.message)
                }
            }
        }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update { it.copy(busquedaTexto = texto) }
    }

    fun seleccionarEtiqueta(id: Long?) {
        _uiState.update {
            it.copy(etiquetaSeleccionadaId = if (it.etiquetaSeleccionadaId == id) null else id)
        }
    }

    fun toggleSoloBajoStock() {
        _uiState.update { it.copy(soloBajoStock = !it.soloBajoStock) }
    }

    fun toggleMostrarInactivos() {
        _uiState.update { it.copy(mostrarInactivos = !it.mostrarInactivos) }
    }

    fun abrirCrearProducto() {
        if (!puedeEscribir()) return
        _uiState.update { it.copy(dialogo = DialogoInventario.CrearProducto) }
    }

    fun abrirEditarProducto(producto: Producto) {
        if (!puedeEscribir()) return
        _uiState.update { it.copy(dialogo = DialogoInventario.EditarProducto(producto)) }
    }

    fun cambiarEstadoProducto(producto: Producto) {
        val negocioId = SesionManager.usuario.value?.negocioActualId ?: return
        if (!puedeEscribir()) return
        viewModelScope.launch {
            when (val result = safeApiCall {
                if (producto.activo) catalogoApi.desactivarProducto(negocioId, producto.id)
                else catalogoApi.reactivarProducto(negocioId, producto.id)
            }) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            mostrarInactivos = true,
                            mensaje = if (producto.activo) "Producto desactivado" else "Producto reactivado"
                        )
                    }
                    cargarDatos()
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun abrirGestionarEtiquetas() {
        if (!puedeEscribir()) return
        _uiState.update { it.copy(dialogo = DialogoInventario.GestionarEtiquetas) }
    }

    fun abrirReabastecerRapido(producto: Producto) {
        if (!puedeEscribir()) return
        _uiState.update { it.copy(dialogo = DialogoInventario.ReabastecerRapido(producto)) }
    }

    fun abrirAjuste(producto: Producto) {
        if (!puedeEscribir()) return
        _uiState.update { it.copy(dialogo = DialogoInventario.AjustarStock(producto)) }
    }

    fun cerrarDialogo() {
        _uiState.update { it.copy(dialogo = null) }
    }

    fun guardarProducto(
        id: Long?,
        nombre: String,
        codigoBarras: String,
        etiquetaId: Long?,
        proveedorId: Long?,
        unidadId: Long,
        cantidadMinima: Float,
        costo: Double,
        precio: Double,
        descripcion: String?
    ) {
        val negocioId = SesionManager.usuario.value?.negocioActualId ?: return
        if (!puedeEscribir()) return
        val unidad = _uiState.value.unidadesMedida.firstOrNull { it.id == unidadId }
        if (nombre.isBlank() || cantidadMinima < 0 || costo < 0 || precio < 0 ||
            (unidad != null && !validarCantidad(cantidadMinima, unidad.admiteDecimales, permitirCero = true))
        ) {
            _uiState.update { it.copy(mensaje = "Revisa el nombre, los precios y el stock mínimo") }
            return
        }

        viewModelScope.launch {
            val resultado = safeApiCall {
                val categoria = etiquetaId?.takeIf { idCategoria ->
                    _uiState.value.etiquetas.any { it.id == idCategoria }
                }
                if (id == null) {
                    catalogoApi.crearProducto(
                        negocioId,
                        CrearProductoRequestDto(
                            categoriaId = categoria,
                            proveedorId = proveedorId,
                            unidadMedidaId = unidadId,
                            codigoBarras = codigoBarras.ifBlank { null },
                            nombre = nombre.trim(),
                            descripcion = descripcion?.trim()?.ifBlank { null },
                            precioCosto = costo,
                            precioVenta = precio,
                            stockMinimo = cantidadMinima.toDouble()
                        )
                    )
                } else {
                    val actual = _uiState.value.productos.firstOrNull { it.id == id }
                    catalogoApi.actualizarProducto(
                        negocioId,
                        id,
                        ActualizarProductoRequestDto(
                            categoriaId = categoria,
                            proveedorId = proveedorId,
                            codigoBarras = codigoBarras.ifBlank { null },
                            nombre = nombre.trim(),
                            descripcion = descripcion?.trim()?.ifBlank { null },
                            precioCosto = costo,
                            precioVenta = precio,
                            stockMinimo = cantidadMinima.toDouble()
                        )
                    )
                }
            }
            when (resultado) {
                is Resource.Success -> {
                    cerrarDialogo()
                    _uiState.update { it.copy(mensaje = "Producto guardado") }
                    cargarDatos()
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = resultado.message) }
            }
        }
    }

    fun procesarReabastecimientoRapido(productoId: Long, cantidadAumentar: Float, motivo: String?) {
        val negocioId = SesionManager.usuario.value?.negocioActualId ?: return
        if (!puedeEscribir()) return
        val producto = _uiState.value.productos.firstOrNull { it.id == productoId }
        if (producto == null || !validarCantidad(cantidadAumentar, producto.unidadMedida.admiteDecimales)) {
            _uiState.update { it.copy(mensaje = "La cantidad debe ser positiva y respetar la unidad de medida") }
            return
        }
        val request = EntradaRequestDto(productoId, cantidadAumentar.toDouble(), motivo)
        viewModelScope.launch {
            val result = safeApiCall {
                val inicial = movimientosApi.movimientos(
                    negocioId = negocioId,
                    productoId = productoId,
                    page = 0,
                    size = 1
                ).content.isEmpty()
                if (inicial) movimientosApi.registrarStockInicial(negocioId, request)
                else movimientosApi.registrarEntrada(negocioId, request)
            }
            when (result) {
                is Resource.Success -> {
                    cerrarDialogo()
                    _uiState.update { it.copy(mensaje = "Entrada registrada") }
                    cargarDatos()
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun registrarAjuste(productoId: Long, cantidadReal: Float, motivo: String?) {
        val negocioId = SesionManager.usuario.value?.negocioActualId ?: return
        if (!puedeEscribir()) return
        val producto = _uiState.value.productos.firstOrNull { it.id == productoId }
        if (cantidadReal < 0 || producto == null ||
            (cantidadReal > 0 && !validarCantidad(cantidadReal, producto.unidadMedida.admiteDecimales))
        ) {
            _uiState.update { it.copy(mensaje = "La cantidad contada debe ser válida para la unidad de medida") }
            return
        }
        if (cantidadReal == producto.cantidadDisponible) {
            _uiState.update { it.copy(mensaje = "La cantidad contada es igual al stock actual") }
            return
        }
        viewModelScope.launch {
            when (val result = safeApiCall {
                movimientosApi.registrarAjuste(
                    negocioId,
                    AjusteRequestDto(productoId, cantidadReal.toDouble(), motivo)
                )
            }) {
                is Resource.Success -> {
                    cerrarDialogo()
                    _uiState.update { it.copy(mensaje = "Ajuste registrado") }
                    cargarDatos()
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun crearEtiqueta(nombre: String) {
        val negocioId = SesionManager.usuario.value?.negocioActualId ?: return
        if (!puedeEscribir()) return
        if (nombre.isBlank()) {
            _uiState.update { it.copy(mensaje = "El nombre de la categoría es obligatorio") }
            return
        }
        viewModelScope.launch {
            when (val result = safeApiCall {
                catalogoApi.crearCategoria(negocioId, CategoriaRequestDto(nombre.trim()))
            }) {
                is Resource.Success -> {
                    cerrarDialogo()
                    _uiState.update { it.copy(mensaje = "Categoría creada") }
                    cargarDatos()
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    private fun puedeEscribir(): Boolean {
        val rol = SesionManager.usuario.value?.negocioActual?.rol
        if (PermisosNegocio.puedeGestionarCatalogo(rol)) return true
        _uiState.update { it.copy(mensaje = "No tienes permisos para modificar el catálogo o el inventario") }
        return false
    }

    private fun validarCantidad(
        cantidad: Float,
        admiteDecimales: Boolean,
        permitirCero: Boolean = false
    ): Boolean {
        if (cantidad < 0f || (cantidad == 0f && !permitirCero)) return false
        val texto = cantidad.toString()
        val decimales = texto.substringAfter('.', "").trimEnd('0').length
        return (admiteDecimales || cantidad % 1f == 0f) && decimales <= 3
    }

    private data class InventarioData(
        val categorias: List<co.edu.univalle.stocklyapp.features.catalogo.data.dto.CategoriaDto>,
        val unidades: List<co.edu.univalle.stocklyapp.features.catalogo.data.dto.UnidadMedidaDto>,
        val proveedores: List<co.edu.univalle.stocklyapp.features.catalogo.data.dto.ProveedorDto>,
        val productos: List<co.edu.univalle.stocklyapp.features.catalogo.data.dto.ProductoDto>,
        val stock: List<co.edu.univalle.stocklyapp.features.inventario.data.dto.StockDto>
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { InventarioViewModel() }
        }
    }
}
