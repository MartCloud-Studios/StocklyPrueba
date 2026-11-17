package co.edu.univalle.stocklyapp.features.auth.domain.repository

import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.core.session.model.Usuario

interface AuthRepository {
    suspend fun registrar(
        nombreUsuario: String,
        correo: String,
        contrasena: String,
        nombreNegocio: String
    ): Resource<Unit>

    suspend fun login(correo: String, contrasena: String): Resource<Usuario>

    suspend fun recuperarContrasena(correo: String): Resource<Unit>
}
