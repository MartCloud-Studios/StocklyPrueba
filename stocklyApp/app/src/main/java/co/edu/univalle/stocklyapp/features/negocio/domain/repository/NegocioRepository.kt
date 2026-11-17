package co.edu.univalle.stocklyapp.features.negocio.domain.repository

import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.features.negocio.domain.model.Rol
import co.edu.univalle.stocklyapp.features.negocio.domain.model.UsuarioNegocio

interface NegocioRepository {
    suspend fun obtenerUsuariosNegocio(): Resource<List<UsuarioNegocio>>
    suspend fun anadirUsuario(correo: String, rol: Rol): Resource<Unit>
    suspend fun cambiarRol(usuarioId: Long, rol: Rol): Resource<Unit>
}
