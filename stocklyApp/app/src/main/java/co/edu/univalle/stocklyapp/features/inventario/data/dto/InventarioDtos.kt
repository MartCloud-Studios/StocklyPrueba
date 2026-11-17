package co.edu.univalle.stocklyapp.features.inventario.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockDto(
    val productoId: Long,
    val nombre: String,
    val unidadMedida: String,
    val cantidad: Double,
    val stockMinimo: Double,
    val bajoMinimo: Boolean
)

@JsonClass(generateAdapter = true)
data class MovimientoDto(
    val id: Long,
    val productoId: Long,
    val productoNombre: String,
    val tipo: String,
    val cantidad: Double,
    val stockAnterior: Double,
    val stockResultante: Double,
    val motivo: String? = null,
    val referenciaTipo: String? = null,
    val referenciaId: Long? = null,
    val usuarioId: Long,
    val usuarioNombre: String,
    val createdAt: String
)

@JsonClass(generateAdapter = true)
data class EntradaRequestDto(
    val productoId: Long,
    val cantidad: Double,
    val motivo: String?
)

@JsonClass(generateAdapter = true)
data class AjusteRequestDto(
    val productoId: Long,
    val cantidadReal: Double,
    val motivo: String?
)

@JsonClass(generateAdapter = true)
data class PaginaDto<T>(
    val content: List<T> = emptyList(),
    val last: Boolean = true
)
