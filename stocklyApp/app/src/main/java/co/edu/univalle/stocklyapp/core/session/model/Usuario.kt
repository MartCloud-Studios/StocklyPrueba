package co.edu.univalle.stocklyapp.core.session.model

data class Usuario(
    val id: Long,
    val nombreUsuario: String,
    val correo: String,
    val negocios: List<NegocioAcceso>,
    val negocioActualId: Long,
    val accessToken: String? = null,
    val tokenType: String = "Bearer"
) {
    val negocioActual: NegocioAcceso?
        get() = negocios.firstOrNull { it.negocioId == negocioActualId }
}

data class NegocioAcceso(
    val negocioId: Long,
    val nombre: String,
    val rol: String
)
