package co.edu.univalle.stocklyapp.features.inventario.domain.model

/** Categoría o etiqueta de producto con soporte jerárquico (Padre / Hija). */
data class Etiqueta(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null,
    val idPadre: Long? = null,
    val nombrePadre: String? = null,
    val activo: Boolean = true
)

/** Unidad de medida (Unidades, Cajas, Kg, Litros, etc.) */
data class UnidadMedida(
    val id: Long,
    val nombre: String,
    val abreviatura: String,
    val esPersonalizada: Boolean = false,
    val admiteDecimales: Boolean = esPersonalizada
)

/** Producto en el catálogo del negocio. */
data class Producto(
    val id: Long,
    val negocioId: Long,
    val nombre: String,
    val codigoBarras: String? = null,
    val imagenUrl: String? = null,
    val etiqueta: Etiqueta? = null,
    val unidadMedida: UnidadMedida,
    val cantidadDisponible: Float,
    val cantidadMinima: Float,
    val costoActual: Double,
    val precioVenta: Double,
    val fechaIngreso: String = "",
    val descripcion: String? = null,
    val proveedorId: Long? = null,
    val activo: Boolean = true
) {
    val bajoStock: Boolean
        get() = cantidadDisponible <= cantidadMinima
}
