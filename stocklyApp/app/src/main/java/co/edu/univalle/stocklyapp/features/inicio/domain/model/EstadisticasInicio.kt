package co.edu.univalle.stocklyapp.features.inicio.domain.model

/** Estadísticas derivadas de existencias y ventas del negocio. */
data class AlertaStockBajo(
    val productoNombre: String,
    val cantidadActual: Float,
    val cantidadMinima: Float,
    val unidad: String
)

data class ProductoTop(
    val productoNombre: String,
    val cantidadVendida: Float,
    val totalGenerado: Double,
    val unidad: String
)

data class EvolucionStockPunto(
    val dia: String,
    val stockTotal: Float
)

data class EstadisticasInicio(
    val alertasBajoStock: List<AlertaStockBajo>,
    val topMasVendidos: List<ProductoTop>,
    val topMenosVendidos: List<ProductoTop>,
    val evolucionStock: List<EvolucionStockPunto>
)
