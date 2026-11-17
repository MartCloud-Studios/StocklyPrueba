package co.edu.univalle.stocklyapp.features.ventas.domain.model

import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto

/** Método de pago aceptado. */
enum class MetodoPago(val id: Long, val etiqueta: String) {
    EFECTIVO(1, "Efectivo"),
    TARJETA(2, "Tarjeta");

    companion object {
        fun desdeClave(clave: String): MetodoPago =
            entries.firstOrNull { it.name.equals(clave, ignoreCase = true) } ?: EFECTIVO
    }
}

/** Detalle de un ítem en la venta. */
data class ItemVenta(
    val producto: Producto,
    val cantidad: Float,
    val precioUnitario: Double,
    val costoUnitario: Double = producto.costoActual
) {
    val subtotal: Double
        get() = cantidad * precioUnitario
}

/** Registro de venta realizada por Vendedor o Propietario. */
data class Venta(
    val id: Long,
    val negocioId: Long,
    val numeroVenta: String,
    val usuarioNombre: String,
    val items: List<ItemVenta>,
    val totalVenta: Double,
    val metodoPago: MetodoPago,
    val fecha: String
)
