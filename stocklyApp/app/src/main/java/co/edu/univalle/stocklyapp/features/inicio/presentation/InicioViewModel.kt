package co.edu.univalle.stocklyapp.features.inicio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.univalle.stocklyapp.app.di.AppContainer
import co.edu.univalle.stocklyapp.core.session.SesionManager
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.core.util.Validadores
import co.edu.univalle.stocklyapp.core.session.model.NegocioAcceso
import co.edu.univalle.stocklyapp.features.inicio.data.repository.EstadisticasInicioRepository
import co.edu.univalle.stocklyapp.features.negocio.domain.model.Rol
import co.edu.univalle.stocklyapp.features.negocio.domain.model.UsuarioNegocio
import co.edu.univalle.stocklyapp.features.negocio.domain.repository.NegocioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InicioViewModel(
    private val repository: NegocioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        cargarInicial()
    }

    fun cargarInicial() {
        val usuario = SesionManager.usuario.value
        val negocio = usuario?.negocioActual
        if (usuario == null || negocio == null) {
            _uiState.update { it.copy(sinSesion = true, cargandoLista = false) }
        } else {
            val puedeAdministrar = negocio.rol.equals("PROPIETARIO", ignoreCase = true)
            _uiState.update {
                it.copy(
                    nombreUsuario = usuario.nombreUsuario,
                    correoUsuario = usuario.correo,
                    nombreNegocio = negocio.nombre,
                    rolActual = negocio.rol.lowercase().replace('_', ' '),
                    puedeAdministrarUsuarios = puedeAdministrar,
                    cargandoLista = puedeAdministrar
                )
            }
            if (puedeAdministrar) {
                cargarUsuarios()
            }
            cargarEstadisticasDashboard(negocio.negocioId)
        }
    }

    private fun cargarEstadisticasDashboard(negocioId: Long) {
        viewModelScope.launch {
            when (val result = EstadisticasInicioRepository.cargar(negocioId)) {
                is Resource.Success -> _uiState.update {
                    it.copy(estadisticasDashboard = result.data, mensaje = null)
                }
                is Resource.Error -> _uiState.update { it.copy(mensaje = result.message) }
            }
        }
    }

    fun cargarUsuarios(mostrarCarga: Boolean = true) {
        if (!_uiState.value.puedeAdministrarUsuarios) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(cargandoLista = mostrarCarga || it.usuariosNegocio.isEmpty(), errorLista = null)
            }
            when (val r = repository.obtenerUsuariosNegocio()) {
                is Resource.Success ->
                    _uiState.update { it.copy(cargandoLista = false, usuariosNegocio = r.data) }
                is Resource.Error ->
                    _uiState.update { it.copy(cargandoLista = false, errorLista = r.message) }
            }
        }
    }

    fun onCorreoBusquedaChange(v: String) =
        _uiState.update { it.copy(correoBusqueda = v, errorBusqueda = null) }

    fun buscar() {
        val state = _uiState.value
        val correo = state.correoBusqueda.trim()
        val error = when {
            correo.isBlank() -> "Escribe un correo para añadir"
            !Validadores.esCorreoValido(correo) -> "Correo no válido"
            correo.equals(state.correoUsuario, ignoreCase = true) ->
                "Ese correo ya pertenece al propietario"
            state.usuariosNegocio.any { it.correo.equals(correo, ignoreCase = true) } ->
                "Ese usuario ya pertenece a tu negocio"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorBusqueda = error) }
            return
        }

        _uiState.update {
            it.copy(errorBusqueda = null, dialogo = DialogoInicio.RolParaNuevo(correo))
        }
    }

    fun anadirConRol(rol: Rol) {
        val dialogo = _uiState.value.dialogo as? DialogoInicio.RolParaNuevo ?: return
        if (_uiState.value.ocupado) return

        viewModelScope.launch {
            _uiState.update { it.copy(ocupado = true) }
            when (val result = repository.anadirUsuario(dialogo.correo, rol)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            ocupado = false,
                            dialogo = null,
                            correoBusqueda = "",
                            mensaje = "Usuario añadido como ${rol.etiqueta}"
                        )
                    }
                    cargarUsuarios(mostrarCarga = false)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            ocupado = false,
                            dialogo = null,
                            mensaje = result.message
                        )
                    }
                }
            }
        }
    }

    fun abrirCambiarRol(u: UsuarioNegocio) {
        _uiState.update { it.copy(dialogo = DialogoInicio.CambiarRol(u)) }
    }

    fun cambiarRol(nuevoRol: Rol) {
        val dialogo = _uiState.value.dialogo as? DialogoInicio.CambiarRol ?: return
        if (_uiState.value.ocupado) return

        viewModelScope.launch {
            _uiState.update { it.copy(ocupado = true) }
            when (val r = repository.cambiarRol(dialogo.usuario.id, nuevoRol)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            ocupado = false,
                            dialogo = null,
                            mensaje = "Rol actualizado a ${nuevoRol.etiqueta}"
                        )
                    }
                    cargarUsuarios(mostrarCarga = false)
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            ocupado = false,
                            dialogo = null,
                            mensaje = r.message
                        )
                    }
                }
            }
        }
    }

    fun abrirOtrosRoles() {
        val otros = SesionManager.usuario.value?.negocios ?: emptyList()
        if (otros.isEmpty()) {
            _uiState.update { it.copy(dialogo = DialogoInicio.SinRolesEnOtrosNegocios) }
        } else {
            _uiState.update { it.copy(dialogo = DialogoInicio.OtrosRoles(otros)) }
        }
    }

    fun seleccionarNegocio(item: NegocioAcceso) {
        SesionManager.seleccionarNegocio(item.negocioId)
        cerrarDialogo()
        cargarInicial()
    }

    fun confirmarCerrarSesion() {
        _uiState.update { it.copy(dialogo = DialogoInicio.ConfirmarCerrarSesion) }
    }

    fun cerrarSesion() {
        SesionManager.cerrar()
        _uiState.update { it.copy(sinSesion = true, dialogo = null) }
    }

    fun cerrarDialogo() {
        _uiState.update { it.copy(dialogo = null) }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                InicioViewModel(AppContainer.negocioRepository)
            }
        }
    }
}
