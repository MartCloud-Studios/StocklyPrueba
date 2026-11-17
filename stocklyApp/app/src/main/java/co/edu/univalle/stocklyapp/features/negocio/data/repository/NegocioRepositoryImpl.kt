package co.edu.univalle.stocklyapp.features.negocio.data.repository

import co.edu.univalle.stocklyapp.core.network.safeApiCall
import co.edu.univalle.stocklyapp.core.session.SesionManager
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.features.negocio.data.api.NegocioApi
import co.edu.univalle.stocklyapp.features.negocio.data.dto.AnadirUsuarioRequestDto
import co.edu.univalle.stocklyapp.features.negocio.data.dto.CambiarRolRequestDto
import co.edu.univalle.stocklyapp.features.negocio.data.mapper.toDomainOrNull
import co.edu.univalle.stocklyapp.features.negocio.domain.model.Rol
import co.edu.univalle.stocklyapp.features.negocio.domain.model.UsuarioNegocio
import co.edu.univalle.stocklyapp.features.negocio.domain.repository.NegocioRepository

class NegocioRepositoryImpl(private val api: NegocioApi) : NegocioRepository {

    override suspend fun obtenerUsuariosNegocio(): Resource<List<UsuarioNegocio>> =
        safeApiCall {
            api.obtenerUsuarios(negocioActualId())
                .filter { it.activo }
                .mapNotNull { it.toDomainOrNull() }
        }

    override suspend fun anadirUsuario(correo: String, rol: Rol): Resource<Unit> =
        safeApiCall(
            mensajePorCodigo = { codigo ->
                when (codigo) {
                    403 -> "Solo el propietario puede gestionar usuarios"
                    404 -> "No existe un usuario con ese correo"
                    409 -> "Ese usuario ya pertenece a tu negocio"
                    else -> null
                }
            }
        ) {
            api.anadirUsuario(
                negocioActualId(),
                AnadirUsuarioRequestDto(email = correo, rol = rol.clave)
            )
            Unit
        }

    override suspend fun cambiarRol(usuarioId: Long, rol: Rol): Resource<Unit> =
        safeApiCall(
            mensajePorCodigo = { codigo ->
                when (codigo) {
                    403 -> "Solo el propietario puede gestionar usuarios"
                    404 -> "Ese usuario ya no pertenece a tu negocio"
                    409 -> "El negocio debe conservar al menos un propietario activo"
                    else -> null
                }
            }
        ) {
            api.cambiarRol(
                negocioActualId(),
                usuarioId,
                CambiarRolRequestDto(rol.clave)
            )
            Unit
        }

    private fun negocioActualId(): Long =
        checkNotNull(SesionManager.usuario.value?.negocioActual?.negocioId) {
            "No hay un negocio seleccionado"
        }
}
