package co.edu.univalle.stocklyapp.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.univalle.stocklyapp.app.di.AppContainer
import co.edu.univalle.stocklyapp.core.session.SesionManager
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.core.util.Validadores
import co.edu.univalle.stocklyapp.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(v: String) =
        _uiState.update { it.copy(correo = v, errorCorreo = null, errorGeneral = null) }

    fun onContrasenaChange(v: String) =
        _uiState.update { it.copy(contrasena = v, errorContrasena = null, errorGeneral = null) }

    fun iniciarSesion() {
        val s = _uiState.value
        if (s.cargando) return

        val errorCorreo = when {
            s.correo.isBlank() -> "El correo es obligatorio"
            !Validadores.esCorreoValido(s.correo.trim()) -> "Correo no válido"
            else -> null
        }
        val errorContrasena = if (s.contrasena.isBlank()) "La contraseña es obligatoria" else null

        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            when (val r = repository.login(s.correo.trim(), s.contrasena)) {
                is Resource.Success -> {
                    SesionManager.iniciar(r.data)
                    _uiState.update { it.copy(cargando = false, loginExitoso = true) }
                }
                is Resource.Error ->
                    _uiState.update { it.copy(cargando = false, errorGeneral = r.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { LoginViewModel(AppContainer.authRepository) }
        }
    }
}
