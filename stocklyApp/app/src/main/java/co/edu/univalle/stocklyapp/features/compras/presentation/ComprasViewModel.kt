package co.edu.univalle.stocklyapp.features.compras.presentation

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
import co.edu.univalle.stocklyapp.features.catalogo.data.dto.ProveedorRequestDto
import co.edu.univalle.stocklyapp.features.catalogo.data.mapper.toProducto
import co.edu.univalle.stocklyapp.features.compras.data.dto.CrearCompraDto
import co.edu.univalle.stocklyapp.features.compras.data.dto.LineaCompraDto
import co.edu.univalle.stocklyapp.features.compras.domain.model.Compra
import co.edu.univalle.stocklyapp.features.compras.domain.model.EstadoCompra
import co.edu.univalle.stocklyapp.features.compras.domain.model.ItemCompra
import co.edu.univalle.stocklyapp.features.compras.domain.model.Proveedor
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComprasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ComprasUiState())
    val uiState: StateFlow<ComprasUiState> = _uiState.asStateFlow()
    private val api = AppContainer.comprasApi
    private val catalogoApi = AppContainer.catalogoApi
    private val inventarioApi = AppContainer.inventarioApi

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
        if (!PermisosNegocio.puedeConsultarCompras(negocio.rol)) {
            _uiState.update { it.copy(sinSesion = false, cargando = false, mensaje = "No tienes acceso al módulo de compras") }
            return
        }

        _uiState.update {
            it.copy(
                nombreNegocio = negocio.nombre,
                puedeConsultar = true,
                cargando = true,
                sinSesion = false
            )
        }
        viewModelScope.launch {
            when (val result = safeApiCall {
                val proveedores = catalogoApi.proveedores(negocio.negocioId)
                val productosCatalogo = catalogoApi.productos(negocio.negocioId, soloActivos = false)
                val stock = inventarioApi.stock(negocio.negocioId).associateBy { it.productoId }
                val comprasResumen = api.compras(negocio.negocioId).content
                val productosHistorial = productosCatalogo.map { it.toProducto(stock[it.id]) }
                val compras = coroutineScope {
                    comprasResumen.map { resumen ->
                        async {
                            val compra = api.compra(negocio.negocioId, resumen.id)
                            Compra(
                                id = compra.id,
                                negocioId = compra.negocioId,
                                numeroFacturaProveedor = compra.numeroFactura ?: "No especificada",
                                proveedor = compra.proveedorId?.let { proveedorId ->
                                    Proveedor(
                                        id = proveedorId,
                                        negocioId = compra.negocioId,
                                        nombre = compra.proveedorNombre.orEmpty()
                                    )
                                },
                                usuarioNombre = compra.usuarioNombre,
                                items = compra.lineas.mapNotNull { linea ->
                                    productosHistorial.firstOrNull { it.id == linea.productoId }?.let { producto ->
                                        ItemCompra(producto, linea.cantidad.toFloat(), linea.precioCosto)
                                    }
                                },
                                totalCosto = compra.total,
                                estado = EstadoCompra.CONFIRMADA,
                                fecha = compra.createdAt
                            )
                        }
                    }.awaitAll()
                }
                ComprasData(
                    proveedores.map {
                        Proveedor(it.id, negocio.negocioId, it.nombre, it.telefono, it.email, activo = it.activo)
                    }.filter { it.activo },
                    productosHistorial.filter { it.activo },
                    compras
                )
            }) {
                is Resource.Success -> _uiState.update {
                    it.copy(
                        proveedores = result.data.proveedores,
                        productosDisponibles = result.data.productos,
                        comprasHistorial = result.data.compras,
                        cargando = false
                    )
                }
                is Resource.Error -> _uiState.update {
                    it.copy(cargando = false, mensaje = result.message)
                }
            }
        }
    }

    fun seleccionarProveedor(proveedor: Proveedor?) {
        _uiState.update { it.copy(proveedorSeleccionado = proveedor) }
    }

    fun onNumeroFacturaChange(texto: String) {
        _uiState.update { it.copy(numeroFactura = texto) }
    }

    fun agregarProductoACompra(producto: co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto, cantidad: Float, costoUnitario: Double) {
        val decimales = cantidad.toString().substringAfter('.', "").trimEnd('0').length
        val decimalesCosto = costoUnitario.toString().substringAfter('.', "").length
        if (cantidad <= 0 || (!producto.unidadMedida.admiteDecimales && cantidad % 1f != 0f) ||
            decimales > 3 || costoUnitario < 0 || decimalesCosto > 2
        ) {
            _uiState.update {
                it.copy(mensaje = "Revisa la cantidad según la unidad y el costo (máximo 2 decimales)")
            }
            return
        }
        val lineas = _uiState.value.itemsCompra.toMutableList()
        val indice = lineas.indexOfFirst { it.producto.id == producto.id }
        if (indice >= 0) {
            lineas[indice] = lineas[indice].copy(
                cantidad = lineas[indice].cantidad + cantidad,
                costoUnitario = costoUnitario
            )
        } else {
            lineas.add(ItemCompra(producto, cantidad, costoUnitario))
        }
        _uiState.update { it.copy(itemsCompra = lineas) }
    }

    fun eliminarItemCompra(indice: Int) {
        val lineas = _uiState.value.itemsCompra.toMutableList()
        if (indice in lineas.indices) {
            lineas.removeAt(indice)
            _uiState.update { it.copy(itemsCompra = lineas) }
        }
    }

    fun abrirCrearProveedor() {
        if (puedeEscribir()) _uiState.update { it.copy(mostrandoCrearProveedor = true) }
    }

    fun cerrarCrearProveedor() {
        _uiState.update { it.copy(mostrandoCrearProveedor = false) }
    }

    fun guardarProveedor(nombre: String, telefono: String, email: String) {
        val negocioId = SesionManager.usuario.value?.negocioActualId ?: return
        if (!puedeEscribir()) return
        if (nombre.isBlank()) {
            _uiState.update { it.copy(mensaje = "El nombre del proveedor es obligatorio") }
            return
        }
        viewModelScope.launch {
            when (val result = safeApiCall {
                catalogoApi.crearProveedor(
                    negocioId,
                    ProveedorRequestDto(
                        nombre.trim(),
                        telefono.ifBlank { null },
                        email.ifBlank { null }
                    )
                )
            }) {
                is Resource.Success -> {
                    cerrarCrearProveedor()
                    _uiState.update { it.copy(mensaje = "Proveedor creado") }
                    cargarDatos()
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun registrarCompra() {
        val usuario = SesionManager.usuario.value
        val negocioId = usuario?.negocioActualId ?: return
        if (!puedeEscribir()) return
        val state = _uiState.value
        if (state.itemsCompra.isEmpty()) {
            _uiState.update { it.copy(mensaje = "Agrega al menos un producto a la compra") }
            return
        }

        viewModelScope.launch {
            when (val result = safeApiCall {
                api.registrar(
                    negocioId,
                    CrearCompraDto(
                        proveedorId = state.proveedorSeleccionado?.id,
                        numeroFactura = state.numeroFactura.trim().ifBlank { null },
                        lineas = state.itemsCompra.map {
                            LineaCompraDto(it.producto.id, it.cantidad.toDouble(), it.costoUnitario)
                        }
                    )
                )
            }) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            itemsCompra = emptyList(),
                            numeroFactura = "",
                            proveedorSeleccionado = null,
                            mensaje = "Compra registrada"
                        )
                    }
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
        if (PermisosNegocio.puedeConsultarCompras(rol)) return true
        _uiState.update { it.copy(mensaje = "No tienes permisos para registrar compras") }
        return false
    }

    private data class ComprasData(
        val proveedores: List<Proveedor>,
        val productos: List<co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto>,
        val compras: List<Compra>
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ComprasViewModel() }
        }
    }
}
