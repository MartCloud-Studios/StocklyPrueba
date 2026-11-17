package co.edu.univalle.stocklyapp.features.compras.presentation

import co.edu.univalle.stocklyapp.features.compras.domain.model.Compra
import co.edu.univalle.stocklyapp.features.compras.domain.model.ItemCompra
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.compras.domain.model.Proveedor

data class ComprasUiState(
    val nombreNegocio: String = "",
    val puedeConsultar: Boolean = false,
    val proveedores: List<Proveedor> = emptyList(),
    val productosDisponibles: List<Producto> = emptyList(),
    val comprasHistorial: List<Compra> = emptyList(),

    // Formulario de compra en proceso
    val proveedorSeleccionado: Proveedor? = null,
    val numeroFactura: String = "",
    val itemsCompra: List<ItemCompra> = emptyList(),

    val cargando: Boolean = true,
    val mostrandoCrearProveedor: Boolean = false,
    val mensaje: String? = null,
    val sinSesion: Boolean = false
) {
    val totalCostoCompra: Double
        get() = itemsCompra.sumOf { it.subtotal }
}
