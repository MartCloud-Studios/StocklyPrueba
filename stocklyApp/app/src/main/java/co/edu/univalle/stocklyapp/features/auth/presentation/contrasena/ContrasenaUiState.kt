package co.edu.univalle.stocklyapp.features.auth.presentation.contrasena

data class ContrasenaUiState(
    val correo: String = "",
    val errorCorreo: String? = null,
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val mensajeExito: String? = null
)
