package co.edu.univalle.stocklyapp.features.ajustes.presentation

import co.edu.univalle.stocklyapp.features.inicio.domain.model.EstadisticasInicio
import co.edu.univalle.stocklyapp.core.session.model.NegocioAcceso

data class AjustesUiState(
    val nombreUsuario: String = "",
    val correoUsuario: String = "",
    val nombreNegocio: String = "",
    val rolActual: String = "",
    val negociosAcceso: List<NegocioAcceso> = emptyList(),
    val estadisticasDashboard: EstadisticasInicio? = null,

    val mostrandoCambiarNegocio: Boolean = false,
    val mostrandoConfirmarCerrarSesion: Boolean = false,
    val mensaje: String? = null,
    val sinSesion: Boolean = false
)
