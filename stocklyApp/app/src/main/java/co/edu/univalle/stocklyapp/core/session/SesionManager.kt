package co.edu.univalle.stocklyapp.core.session

import co.edu.univalle.stocklyapp.core.session.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sesión en memoria. Se pierde si Android mata el proceso.
 */
object SesionManager {
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    fun iniciar(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun seleccionarNegocio(negocioId: Long) {
        val usuario = _usuario.value ?: return
        if (usuario.negocios.none { it.negocioId == negocioId }) return
        _usuario.value = usuario.copy(negocioActualId = negocioId)
    }

    fun cerrar() {
        _usuario.value = null
    }
}
