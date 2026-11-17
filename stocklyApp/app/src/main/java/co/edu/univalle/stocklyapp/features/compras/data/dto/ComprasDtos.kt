package co.edu.univalle.stocklyapp.features.compras.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LineaCompraDto(
    val productoId: Long,
    val cantidad: Double,
    val precioCosto: Double
)

@JsonClass(generateAdapter = true)
data class CrearCompraDto(
    val proveedorId: Long?,
    val numeroFactura: String?,
    val lineas: List<LineaCompraDto>
)

@JsonClass(generateAdapter = true)
data class DetalleCompraDto(
    val productoId: Long,
    val productoNombre: String,
    val cantidad: Double,
    val precioCosto: Double,
    val subtotal: Double
)

@JsonClass(generateAdapter = true)
data class CompraDto(
    val id: Long,
    val negocioId: Long,
    val proveedorId: Long? = null,
    val proveedorNombre: String? = null,
    val usuarioId: Long,
    val usuarioNombre: String,
    val numeroFactura: String? = null,
    val total: Double,
    val estado: String,
    val createdAt: String,
    val lineas: List<DetalleCompraDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CompraResumenDto(
    val id: Long,
    val proveedorId: Long? = null,
    val proveedorNombre: String? = null,
    val usuarioId: Long,
    val usuarioNombre: String,
    val numeroFactura: String? = null,
    val total: Double,
    val estado: String,
    val createdAt: String
)
