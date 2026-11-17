package co.edu.univalle.stocklyapp.features.inventario.presentation

import co.edu.univalle.stocklyapp.features.inventario.domain.model.Etiqueta
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.inventario.domain.model.UnidadMedida
import co.edu.univalle.stocklyapp.features.compras.domain.model.Proveedor
import co.edu.univalle.stocklyapp.core.session.PermisosNegocio

sealed interface DialogoInventario {
    data object CrearProducto : DialogoInventario
    data class EditarProducto(val producto: Producto) : DialogoInventario
    data object GestionarEtiquetas : DialogoInventario
    data class ReabastecerRapido(val producto: Producto) : DialogoInventario
    data class AjustarStock(val producto: Producto) : DialogoInventario
}

data class InventarioUiState(
    val nombreNegocio: String = "",
    val rolActual: String = "",
    val productos: List<Producto> = emptyList(),
    val etiquetas: List<Etiqueta> = emptyList(),
    val unidadesMedida: List<UnidadMedida> = emptyList(),
    val proveedores: List<Proveedor> = emptyList(),

    val etiquetaSeleccionadaId: Long? = null,
    val busquedaTexto: String = "",
    val soloBajoStock: Boolean = false,
    val mostrarInactivos: Boolean = false,

    val cargando: Boolean = true,
    val error: String? = null,
    val dialogo: DialogoInventario? = null,
    val mensaje: String? = null,
    val sinSesion: Boolean = false
) {
    val productosFiltrados: List<Producto>
        get() = productos.filter { prod ->
            val coincideActivo = mostrarInactivos || prod.activo
            val coincideEtiqueta = etiquetaSeleccionadaId == null || prod.etiqueta?.id == etiquetaSeleccionadaId
            val coincideTexto = busquedaTexto.isBlank() || prod.nombre.contains(busquedaTexto, ignoreCase = true) ||
                    (prod.codigoBarras?.contains(busquedaTexto, ignoreCase = true) == true)
            val coincideBajoStock = !soloBajoStock || (prod.activo && prod.bajoStock)
            coincideActivo && coincideEtiqueta && coincideTexto && coincideBajoStock
        }

    val esPropietario: Boolean
        get() = rolActual.equals("PROPIETARIO", ignoreCase = true)

    val esAlmacenista: Boolean
        get() = PermisosNegocio.puedeGestionarInventario(rolActual)

    val esVendedor: Boolean
        get() = PermisosNegocio.puedeRegistrarVentas(rolActual)
}
