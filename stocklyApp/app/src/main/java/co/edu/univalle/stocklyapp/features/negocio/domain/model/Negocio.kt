package co.edu.univalle.stocklyapp.features.negocio.domain.model

/** Usuario que ya pertenece al negocio, con su rol. */
data class UsuarioNegocio(
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: Rol
)

/** Resultado de buscar un usuario por correo (aún no pertenece al negocio). */
data class UsuarioBuscado(
    val id: Long,
    val nombre: String,
    val correo: String
)

/** Rol que el usuario logueado tiene en el negocio de otra persona. */
data class RolEnNegocio(
    val negocioId: Long,
    val nombreNegocio: String,
    val rol: Rol
)
