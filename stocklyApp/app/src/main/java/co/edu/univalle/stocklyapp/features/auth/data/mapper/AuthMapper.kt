package co.edu.univalle.stocklyapp.features.auth.data.mapper

import co.edu.univalle.stocklyapp.core.session.model.NegocioAcceso
import co.edu.univalle.stocklyapp.core.session.model.Usuario
import co.edu.univalle.stocklyapp.features.auth.data.dto.LoginResponseDto
import co.edu.univalle.stocklyapp.features.auth.data.dto.NegocioAccesoDto

fun NegocioAccesoDto.toDomain(): NegocioAcceso =
    NegocioAcceso(negocioId = negocioId, nombre = nombre, rol = rol)

fun LoginResponseDto.toDomain(correo: String): Usuario? {
    val negocios = negocios.map { it.toDomain() }
    val negocioInicial = negocios.firstOrNull() ?: return null

    return Usuario(
        id = usuarioId,
        nombreUsuario = nombre,
        correo = correo.trim().lowercase(),
        negocios = negocios,
        negocioActualId = negocioInicial.negocioId,
        accessToken = accessToken,
        tokenType = tokenType
    )
}
