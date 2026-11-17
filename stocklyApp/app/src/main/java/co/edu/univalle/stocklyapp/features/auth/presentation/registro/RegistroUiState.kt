package co.edu.univalle.stocklyapp.features.auth.presentation.registro

data class RegistroUiState(
    val nombreUsuario: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val nombreNegocio: String = "",
    val aceptaCondiciones: Boolean = false,

    val errorNombreUsuario: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,
    val errorNombreNegocio: String? = null,
    val errorCondiciones: String? = null,

    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val registroExitoso: Boolean = false
)
