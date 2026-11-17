package co.edu.univalle.stocklyapp.features.auth.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistroRequestDto(
    val nombre: String,
    val email: String,
    val password: String,
    val nombreNegocio: String
)

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    val usuarioId: Long,
    val nombre: String,
    val negocios: List<NegocioAccesoDto>,
    val accessToken: String? = null,
    val tokenType: String = "Bearer"
)

@JsonClass(generateAdapter = true)
data class NegocioAccesoDto(
    val negocioId: Long,
    val nombre: String,
    val rol: String
)
