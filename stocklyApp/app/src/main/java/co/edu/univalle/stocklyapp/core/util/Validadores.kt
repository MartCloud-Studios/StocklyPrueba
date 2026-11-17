package co.edu.univalle.stocklyapp.core.util

import android.util.Patterns

object Validadores {
    const val MIN_CONTRASENA = 6

    fun esCorreoValido(correo: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(correo).matches()
}
