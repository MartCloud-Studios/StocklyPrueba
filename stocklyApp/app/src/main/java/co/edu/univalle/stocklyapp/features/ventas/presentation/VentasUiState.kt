package co.edu.univalle.stocklyapp.features.ventas.presentation

import co.edu.univalle.stocklyapp.features.ventas.domain.model.ItemVenta
import co.edu.univalle.stocklyapp.features.ventas.domain.model.MetodoPago
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.ventas.domain.model.Venta

data class VentasUiState(
    val nombreNegocio: String = "",
    val rolActual: String = "",
    val productosDisponibles: List<Producto> = emptyList(),
    val ventasHistorial: List<Venta> = emptyList(),

    // Formulario de venta en proceso
    val metodoPago: MetodoPago = MetodoPago.EFECTIVO,
    val itemsCarrito: List<ItemVenta> = emptyList(),

    val cargando: Boolean = true,
    val comprobanteGenerado: Venta? = null,
    val mensaje: String? = null,
    val sinSesion: Boolean = false
) {
    val puedeRegistrarVentas: Boolean
        get() = co.edu.univalle.stocklyapp.core.session.PermisosNegocio.puedeRegistrarVentas(rolActual)

    val subtotalCarrito: Double
        get() = itemsCarrito.sumOf { it.subtotal }

    val totalVenta: Double
        get() = subtotalCarrito
}
