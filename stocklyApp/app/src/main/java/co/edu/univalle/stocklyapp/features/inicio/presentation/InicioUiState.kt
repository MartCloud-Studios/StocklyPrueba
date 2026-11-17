package co.edu.univalle.stocklyapp.features.inicio.presentation

import co.edu.univalle.stocklyapp.features.inicio.domain.model.EstadisticasInicio
import co.edu.univalle.stocklyapp.core.session.model.NegocioAcceso
import co.edu.univalle.stocklyapp.features.negocio.domain.model.UsuarioNegocio

/** Ventanas emergentes de la pantalla de inicio (solo una a la vez). */
sealed interface DialogoInicio {
    data class RolParaNuevo(val correo: String) : DialogoInicio
    data class CambiarRol(val usuario: UsuarioNegocio) : DialogoInicio
    data object SinRolesEnOtrosNegocios : DialogoInicio
    data class OtrosRoles(val items: List<NegocioAcceso>) : DialogoInicio
    data object ConfirmarCerrarSesion : DialogoInicio
}

data class InicioUiState(
    val nombreUsuario: String = "",
    val correoUsuario: String = "",
    val nombreNegocio: String = "",
    val rolActual: String = "",
    val puedeAdministrarUsuarios: Boolean = false,

    val usuariosNegocio: List<UsuarioNegocio> = emptyList(),
    val estadisticasDashboard: EstadisticasInicio? = null,
    val cargandoLista: Boolean = true,
    val errorLista: String? = null,

    val correoBusqueda: String = "",
    val errorBusqueda: String? = null,

    val ocupado: Boolean = false,
    val dialogo: DialogoInicio? = null,
    val mensaje: String? = null,
    val sinSesion: Boolean = false
)
