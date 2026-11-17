package co.edu.univalle.stocklyapp.features.negocio.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsuarioNegocioDto(
    val usuarioId: Long,
    val nombre: String,
    val email: String,
    val rol: String,
    val activo: Boolean
)

@JsonClass(generateAdapter = true)
data class AnadirUsuarioRequestDto(
    val email: String,
    val rol: String
)

@JsonClass(generateAdapter = true)
data class CambiarRolRequestDto(
    val rol: String
)
