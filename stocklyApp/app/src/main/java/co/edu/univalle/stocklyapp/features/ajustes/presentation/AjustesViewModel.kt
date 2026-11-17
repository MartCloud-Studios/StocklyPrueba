package co.edu.univalle.stocklyapp.features.ajustes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.univalle.stocklyapp.core.session.SesionManager
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.core.session.model.NegocioAcceso
import co.edu.univalle.stocklyapp.features.inicio.data.repository.EstadisticasInicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AjustesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesUiState())
    val uiState: StateFlow<AjustesUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        val usuario = SesionManager.usuario.value
        val negocio = usuario?.negocioActual
        if (usuario == null || negocio == null) {
            _uiState.update { it.copy(sinSesion = true) }
            return
        }

        _uiState.update {
            it.copy(
                nombreUsuario = usuario.nombreUsuario,
                correoUsuario = usuario.correo,
                nombreNegocio = negocio.nombre,
                rolActual = negocio.rol.lowercase(),
                negociosAcceso = usuario.negocios
            )
        }
        viewModelScope.launch {
            when (val result = EstadisticasInicioRepository.cargar(negocio.negocioId)) {
                is Resource.Success -> _uiState.update {
                    it.copy(estadisticasDashboard = result.data)
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun abrirCambiarNegocio() {
        _uiState.update { it.copy(mostrandoCambiarNegocio = true) }
    }

    fun cerrarCambiarNegocio() {
        _uiState.update { it.copy(mostrandoCambiarNegocio = false) }
    }

    fun seleccionarNegocio(negocio: NegocioAcceso) {
        SesionManager.seleccionarNegocio(negocio.negocioId)
        _uiState.update {
            it.copy(
                nombreNegocio = negocio.nombre,
                rolActual = negocio.rol.lowercase(),
                mostrandoCambiarNegocio = false,
                mensaje = "Cambiado al negocio: ${negocio.nombre}"
            )
        }
        cargarDatos()
    }

    fun abrirConfirmarCerrarSesion() {
        _uiState.update { it.copy(mostrandoConfirmarCerrarSesion = true) }
    }

    fun cerrarConfirmarCerrarSesion() {
        _uiState.update { it.copy(mostrandoConfirmarCerrarSesion = false) }
    }

    fun cerrarSesion() {
        SesionManager.cerrar()
        _uiState.update { it.copy(sinSesion = true) }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { AjustesViewModel() }
        }
    }
}
