package co.edu.univalle.stocklyapp.features.auth.presentation.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.univalle.stocklyapp.app.di.AppContainer
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.core.util.Validadores
import co.edu.univalle.stocklyapp.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombreUsuarioChange(v: String) =
        _uiState.update { it.copy(nombreUsuario = v, errorNombreUsuario = null, errorGeneral = null) }

    fun onCorreoChange(v: String) =
        _uiState.update { it.copy(correo = v, errorCorreo = null, errorGeneral = null) }

    fun onContrasenaChange(v: String) =
        _uiState.update { it.copy(contrasena = v, errorContrasena = null, errorGeneral = null) }

    fun onConfirmarContrasenaChange(v: String) =
        _uiState.update { it.copy(confirmarContrasena = v, errorConfirmarContrasena = null, errorGeneral = null) }

    fun onNombreNegocioChange(v: String) =
        _uiState.update { it.copy(nombreNegocio = v, errorNombreNegocio = null, errorGeneral = null) }

    fun onAceptaCondicionesChange(v: Boolean) =
        _uiState.update { it.copy(aceptaCondiciones = v, errorCondiciones = null, errorGeneral = null) }

    fun registrar() {
        val s = _uiState.value
        if (s.cargando) return

        val errorNombre = if (s.nombreUsuario.isBlank()) "El nombre de usuario es obligatorio" else null
        val errorCorreo = when {
            s.correo.isBlank() -> "El correo es obligatorio"
            !Validadores.esCorreoValido(s.correo.trim()) -> "Correo no válido"
            else -> null
        }
        val errorContrasena = when {
            s.contrasena.isBlank() -> "La contraseña es obligatoria"
            s.contrasena.length < Validadores.MIN_CONTRASENA ->
                "Mínimo ${Validadores.MIN_CONTRASENA} caracteres"
            else -> null
        }
        val errorConfirmar = when {
            s.confirmarContrasena.isBlank() -> "Confirma tu contraseña"
            s.confirmarContrasena != s.contrasena -> "Las contraseñas no coinciden"
            else -> null
        }
        val errorNegocio = if (s.nombreNegocio.isBlank()) "El nombre del negocio es obligatorio" else null
        val errorCondiciones =
            if (!s.aceptaCondiciones) "Debes aceptar las condiciones para continuar" else null

        if (listOf(errorNombre, errorCorreo, errorContrasena, errorConfirmar, errorNegocio, errorCondiciones)
                .any { it != null }
        ) {
            _uiState.update {
                it.copy(
                    errorNombreUsuario = errorNombre,
                    errorCorreo = errorCorreo,
                    errorContrasena = errorContrasena,
                    errorConfirmarContrasena = errorConfirmar,
                    errorNombreNegocio = errorNegocio,
                    errorCondiciones = errorCondiciones
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            val resultado = repository.registrar(
                nombreUsuario = s.nombreUsuario.trim(),
                correo = s.correo.trim(),
                contrasena = s.contrasena,
                nombreNegocio = s.nombreNegocio.trim()
            )
            when (resultado) {
                is Resource.Success ->
                    _uiState.update { it.copy(cargando = false, registroExitoso = true) }
                is Resource.Error ->
                    _uiState.update { it.copy(cargando = false, errorGeneral = resultado.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { RegistroViewModel(AppContainer.authRepository) }
        }
    }
}
