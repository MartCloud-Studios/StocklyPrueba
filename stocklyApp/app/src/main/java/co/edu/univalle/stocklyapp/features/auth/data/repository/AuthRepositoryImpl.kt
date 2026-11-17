package co.edu.univalle.stocklyapp.features.auth.data.repository

import co.edu.univalle.stocklyapp.core.network.safeApiCall
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.features.auth.data.api.AuthApi
import co.edu.univalle.stocklyapp.features.auth.data.dto.LoginRequestDto
import co.edu.univalle.stocklyapp.features.auth.data.dto.RegistroRequestDto
import co.edu.univalle.stocklyapp.features.auth.data.mapper.toDomain
import co.edu.univalle.stocklyapp.features.auth.domain.repository.AuthRepository
import co.edu.univalle.stocklyapp.core.session.model.Usuario

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {

    override suspend fun registrar(
        nombreUsuario: String,
        correo: String,
        contrasena: String,
        nombreNegocio: String
    ): Resource<Unit> = safeApiCall(
        mensajePorCodigo = { codigo ->
            if (codigo == 409) "Ese correo ya está registrado" else null
        }
    ) {
        api.registrar(
            RegistroRequestDto(
                nombre = nombreUsuario,
                email = correo,
                password = contrasena,
                nombreNegocio = nombreNegocio,
            )
        )
        Unit
    }

    override suspend fun login(correo: String, contrasena: String): Resource<Usuario> {
        val response = safeApiCall(
            mensajePorCodigo = { codigo ->
                if (codigo == 401) {
                    "Correo o contraseña incorrectos"
                } else null
            }
        ) {
            api.login(LoginRequestDto(email = correo, password = contrasena))
        }

        return when (response) {
            is Resource.Error -> response
            is Resource.Success -> {
                val usuario = response.data.toDomain(correo)
                    ?: return Resource.Error("Tu cuenta no tiene negocios activos")
                Resource.Success(usuario)
            }
        }
    }

    override suspend fun recuperarContrasena(correo: String): Resource<Unit> =
        Resource.Error("El servidor todavía no ofrece recuperación de contraseña")
}
