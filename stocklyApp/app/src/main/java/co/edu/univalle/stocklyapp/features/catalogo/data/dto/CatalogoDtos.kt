package co.edu.univalle.stocklyapp.features.catalogo.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoriaDto(
    val id: Long,
    val nombre: String,
    val activo: Boolean
)

@JsonClass(generateAdapter = true)
data class UnidadMedidaDto(
    val id: Long,
    val nombre: String,
    val base: Boolean,
    val admiteDecimales: Boolean,
    val activo: Boolean
)

@JsonClass(generateAdapter = true)
data class ProveedorDto(
    val id: Long,
    val nombre: String,
    val telefono: String? = null,
    val email: String? = null,
    val activo: Boolean
)

@JsonClass(generateAdapter = true)
data class ProductoDto(
    val id: Long,
    val negocioId: Long,
    val categoriaId: Long? = null,
    val categoriaNombre: String? = null,
    val proveedorId: Long? = null,
    val proveedorNombre: String? = null,
    val unidadMedidaId: Long,
    val unidadMedidaNombre: String,
    val unidadBase: Boolean,
    val codigoBarras: String? = null,
    val nombre: String,
    val descripcion: String? = null,
    val precioCosto: Double,
    val precioVenta: Double,
    val stockMinimo: Double,
    val activo: Boolean,
    val createdAt: String
)

@JsonClass(generateAdapter = true)
data class CategoriaRequestDto(val nombre: String)

@JsonClass(generateAdapter = true)
data class ProveedorRequestDto(
    val nombre: String,
    val telefono: String? = null,
    val email: String? = null
)

@JsonClass(generateAdapter = true)
data class CrearProductoRequestDto(
    val categoriaId: Long?,
    val proveedorId: Long?,
    val unidadMedidaId: Long,
    val codigoBarras: String?,
    val nombre: String,
    val descripcion: String?,
    val precioCosto: Double,
    val precioVenta: Double,
    val stockMinimo: Double
)

@JsonClass(generateAdapter = true)
data class ActualizarProductoRequestDto(
    val categoriaId: Long?,
    val proveedorId: Long?,
    val codigoBarras: String?,
    val nombre: String,
    val descripcion: String?,
    val precioCosto: Double,
    val precioVenta: Double,
    val stockMinimo: Double
)

@JsonClass(generateAdapter = true)
data class NombreRequestDto(val nombre: String)
