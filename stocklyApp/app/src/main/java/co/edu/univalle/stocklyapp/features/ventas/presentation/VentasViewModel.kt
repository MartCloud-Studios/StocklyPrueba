package co.edu.univalle.stocklyapp.features.ventas.presentation

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
import co.edu.univalle.stocklyapp.features.catalogo.data.mapper.toProducto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.UnidadMedida
import co.edu.univalle.stocklyapp.features.ventas.data.dto.CrearVentaDto
import co.edu.univalle.stocklyapp.features.ventas.data.dto.LineaVentaDto
import co.edu.univalle.stocklyapp.features.ventas.domain.model.ItemVenta
import co.edu.univalle.stocklyapp.features.ventas.domain.model.MetodoPago
import co.edu.univalle.stocklyapp.features.ventas.domain.model.Venta
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VentasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VentasUiState())
    val uiState: StateFlow<VentasUiState> = _uiState.asStateFlow()
    private val api = AppContainer.ventasApi
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
        if (!PermisosNegocio.puedeRegistrarVentas(negocio.rol)) {
            _uiState.update { it.copy(cargando = false, sinSesion = false, mensaje = "No tienes acceso al módulo de ventas") }
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
                val catalogo = catalogoApi.productos(negocio.negocioId, soloActivos = true)
                val stock = inventarioApi.stock(negocio.negocioId).associateBy { it.productoId }
                val productos = catalogo.map { it.toProducto(stock[it.id]) }
                val resumenes = api.ventas(negocio.negocioId).content
                val ventas = coroutineScope {
                    resumenes.map { resumen ->
                        async {
                            mapVenta(api.venta(negocio.negocioId, resumen.id), productos)
                        }
                    }.awaitAll()
                }
                VentasData(productos.filter { it.activo }, ventas)
            }) {
                is Resource.Success -> _uiState.update {
                    it.copy(
                        productosDisponibles = result.data.productos,
                        ventasHistorial = result.data.ventas,
                        cargando = false
                    )
                }
                is Resource.Error -> _uiState.update {
                    it.copy(cargando = false, mensaje = result.message)
                }
            }
        }
    }

    fun seleccionarMetodoPago(metodo: MetodoPago) {
        _uiState.update { it.copy(metodoPago = metodo) }
    }

    fun agregarProductoACarrito(producto: Producto, cantidad: Float) {
        val decimales = cantidad.toString().substringAfter('.', "").trimEnd('0').length
        if (cantidad <= 0 || (!producto.unidadMedida.admiteDecimales && cantidad % 1f != 0f) ||
            decimales > 3
        ) {
            _uiState.update { it.copy(mensaje = "La cantidad debe ser positiva y válida para la unidad") }
            return
        }
        if (producto.precioVenta <= 0.0) {
            _uiState.update { it.copy(mensaje = "Este producto tiene precio de venta cero y no se puede vender") }
            return
        }
        val cantidadActual = _uiState.value.itemsCarrito
            .firstOrNull { it.producto.id == producto.id }?.cantidad ?: 0f
        if (cantidadActual + cantidad > producto.cantidadDisponible) {
            _uiState.update {
                it.copy(mensaje = "Stock insuficiente de '${producto.nombre}'. Disponible: ${producto.cantidadDisponible}")
            }
            return
        }
        val lineas = _uiState.value.itemsCarrito.toMutableList()
        val indice = lineas.indexOfFirst { it.producto.id == producto.id }
        if (indice >= 0) {
            val anterior = lineas[indice]
            lineas[indice] = anterior.copy(cantidad = anterior.cantidad + cantidad)
        } else {
            lineas.add(ItemVenta(producto, cantidad, producto.precioVenta))
        }
        _uiState.update { it.copy(itemsCarrito = lineas) }
    }

    fun cambiarCantidadCarrito(indice: Int, nuevaCantidad: Float) {
        val lineas = _uiState.value.itemsCarrito.toMutableList()
        if (indice !in lineas.indices) return
        if (nuevaCantidad <= 0) {
            lineas.removeAt(indice)
        } else {
            val item = lineas[indice]
            if (nuevaCantidad > item.producto.cantidadDisponible) {
                _uiState.update { it.copy(mensaje = "Supera el stock disponible (${item.producto.cantidadDisponible})") }
                return
            }
            lineas[indice] = item.copy(cantidad = nuevaCantidad)
        }
        _uiState.update { it.copy(itemsCarrito = lineas) }
    }

    fun eliminarItemCarrito(indice: Int) {
        val lineas = _uiState.value.itemsCarrito.toMutableList()
        if (indice in lineas.indices) {
            lineas.removeAt(indice)
            _uiState.update { it.copy(itemsCarrito = lineas) }
        }
    }

    fun procesarVenta(metodoPago: MetodoPago = _uiState.value.metodoPago) {
        val state = _uiState.value
        val negocioId = SesionManager.usuario.value?.negocioActualId ?: return
        if (!PermisosNegocio.puedeRegistrarVentas(SesionManager.usuario.value?.negocioActual?.rol)) {
            _uiState.update { it.copy(mensaje = "No tienes permiso para registrar ventas") }
            return
        }
        if (state.itemsCarrito.isEmpty()) {
            _uiState.update { it.copy(mensaje = "El carrito de venta está vacío") }
            return
        }
        viewModelScope.launch {
            when (val result = safeApiCall {
                api.registrar(
                    negocioId,
                    CrearVentaDto(
                        metodoPago = metodoPago.name,
                        lineas = state.itemsCarrito.map {
                            LineaVentaDto(it.producto.id, it.cantidad.toDouble())
                        }
                    )
                )
            }) {
                is Resource.Success -> {
                    val venta = mapVenta(result.data, state.productosDisponibles)
                    _uiState.update {
                        it.copy(
                            itemsCarrito = emptyList(),
                            comprobanteGenerado = venta,
                            mensaje = "Venta ${venta.numeroVenta} registrada"
                        )
                    }
                    cargarDatos()
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun cerrarComprobante() {
        _uiState.update { it.copy(comprobanteGenerado = null) }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    private fun mapVenta(
        dto: co.edu.univalle.stocklyapp.features.ventas.data.dto.VentaDto,
        productos: List<Producto>
    ): Venta {
        val items = dto.lineas.map { linea ->
            val producto = productos.firstOrNull { it.id == linea.productoId } ?: Producto(
                id = linea.productoId,
                negocioId = dto.negocioId,
                nombre = linea.productoNombre,
                unidadMedida = UnidadMedida(0, "Unidad", "Unidad"),
                cantidadDisponible = 0f,
                cantidadMinima = 0f,
                costoActual = linea.precioCosto,
                precioVenta = linea.precioVenta
            )
            ItemVenta(
                producto = producto,
                cantidad = linea.cantidad.toFloat(),
                precioUnitario = linea.precioVenta,
                costoUnitario = linea.precioCosto
            )
        }
        return Venta(
            id = dto.id,
            negocioId = dto.negocioId,
            numeroVenta = dto.id.toString(),
            usuarioNombre = dto.usuarioNombre,
            items = items,
            totalVenta = dto.total,
            metodoPago = MetodoPago.desdeClave(dto.metodoPago),
            fecha = dto.createdAt
        )
    }

    private data class VentasData(val productos: List<Producto>, val ventas: List<Venta>)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { VentasViewModel() }
        }
    }
}
