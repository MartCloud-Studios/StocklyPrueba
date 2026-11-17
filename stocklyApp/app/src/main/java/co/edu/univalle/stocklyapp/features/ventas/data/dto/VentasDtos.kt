package co.edu.univalle.stocklyapp.features.ventas.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LineaVentaDto(val productoId: Long, val cantidad: Double)

@JsonClass(generateAdapter = true)
data class CrearVentaDto(val metodoPago: String, val lineas: List<LineaVentaDto>)

@JsonClass(generateAdapter = true)
data class DetalleVentaDto(
    val productoId: Long,
    val productoNombre: String,
    val cantidad: Double,
    val precioVenta: Double,
    val precioCosto: Double,
    val subtotal: Double
)

@JsonClass(generateAdapter = true)
data class VentaDto(
    val id: Long,
    val negocioId: Long,
    val usuarioId: Long,
    val usuarioNombre: String,
    val metodoPago: String,
    val total: Double,
    val estado: String,
    val createdAt: String,
    val lineas: List<DetalleVentaDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class VentaResumenDto(
    val id: Long,
    val usuarioId: Long,
    val usuarioNombre: String,
    val metodoPago: String,
    val total: Double,
    val estado: String,
    val createdAt: String
)
