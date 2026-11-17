package co.edu.univalle.stocklyapp.features.compras.domain.model

import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto

/** Proveedor de insumos o mercancía. */
data class Proveedor(
    val id: Long,
    val negocioId: Long,
    val nombre: String,
    val telefono: String? = null,
    val email: String? = null,
    val nitCedula: String? = null,
    val activo: Boolean = true
)

/** Estado de una compra/reabastecimiento. */
enum class EstadoCompra(val clave: String, val etiqueta: String) {
    CONFIRMADA("CONFIRMADA", "Confirmada");

    companion object {
        fun desdeClave(clave: String): EstadoCompra =
            entries.firstOrNull { it.clave.equals(clave, ignoreCase = true) } ?: CONFIRMADA
    }
}

/** Detalle de un ítem en la compra. */
data class ItemCompra(
    val producto: Producto,
    val cantidad: Float,
    val costoUnitario: Double
) {
    val subtotal: Double
        get() = cantidad * costoUnitario
}

/** Registro de compra / reabastecimiento realizada por Almacenista o Propietario. */
data class Compra(
    val id: Long,
    val negocioId: Long,
    val numeroFacturaProveedor: String,
    val proveedor: Proveedor?,
    val usuarioNombre: String,
    val items: List<ItemCompra>,
    val totalCosto: Double,
    val estado: EstadoCompra,
    val fecha: String
)
