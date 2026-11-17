package co.edu.univalle.stocklyapp.features.auth.presentation.login

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val loginExitoso: Boolean = false
)
