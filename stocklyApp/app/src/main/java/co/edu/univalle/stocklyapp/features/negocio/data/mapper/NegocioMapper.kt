package co.edu.univalle.stocklyapp.features.negocio.data.mapper

import co.edu.univalle.stocklyapp.features.negocio.data.dto.UsuarioNegocioDto
import co.edu.univalle.stocklyapp.features.negocio.domain.model.Rol
import co.edu.univalle.stocklyapp.features.negocio.domain.model.UsuarioNegocio

fun UsuarioNegocioDto.toDomainOrNull(): UsuarioNegocio? =
    Rol.desdeClave(rol)?.let { rol ->
        UsuarioNegocio(id = usuarioId, nombre = nombre, correo = email, rol = rol)
    }
