package co.edu.univalle.stocklyapp.features.auth.presentation.contrasena

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

class ContrasenaViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContrasenaUiState())
    val uiState: StateFlow<ContrasenaUiState> = _uiState.asStateFlow()

    fun onCorreoChange(v: String) =
        _uiState.update {
            it.copy(correo = v, errorCorreo = null, errorGeneral = null, mensajeExito = null)
        }

    fun enviar() {
        val s = _uiState.value
        if (s.cargando) return

        val errorCorreo = when {
            s.correo.isBlank() -> "El correo es obligatorio"
            !Validadores.esCorreoValido(s.correo.trim()) -> "Correo no válido"
            else -> null
        }
        if (errorCorreo != null) {
            _uiState.update { it.copy(errorCorreo = errorCorreo) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null, mensajeExito = null) }
            when (val r = repository.recuperarContrasena(s.correo.trim())) {
                is Resource.Success -> _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeExito = "Si el correo existe, te enviaremos las instrucciones para recuperar tu contraseña"
                    )
                }
                is Resource.Error ->
                    _uiState.update { it.copy(cargando = false, errorGeneral = r.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ContrasenaViewModel(AppContainer.authRepository) }
        }
    }
}
