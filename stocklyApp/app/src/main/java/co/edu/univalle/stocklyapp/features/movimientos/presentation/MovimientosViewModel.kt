package co.edu.univalle.stocklyapp.features.movimientos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import co.edu.univalle.stocklyapp.app.di.AppContainer
import co.edu.univalle.stocklyapp.core.network.safeApiCall
import co.edu.univalle.stocklyapp.core.session.PermisosNegocio
import co.edu.univalle.stocklyapp.core.session.SesionManager
import co.edu.univalle.stocklyapp.core.util.Resource
import co.edu.univalle.stocklyapp.features.movimientos.domain.model.MovimientoInventario
import co.edu.univalle.stocklyapp.features.movimientos.domain.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovimientosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MovimientosUiState())
    val uiState: StateFlow<MovimientosUiState> = _uiState.asStateFlow()
    private val api = AppContainer.inventarioApi

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        val usuario = SesionManager.usuario.value
        val negocio = usuario?.negocioActual
        if (negocio == null) {
            _uiState.update { it.copy(sinSesion = true, cargando = false) }
            return
        }
        if (!PermisosNegocio.puedeConsultar(negocio.rol)) {
            _uiState.update { it.copy(sinSesion = false, cargando = false, mensaje = "No tienes acceso al historial") }
            return
        }
        _uiState.update {
            it.copy(nombreNegocio = negocio.nombre, cargando = true, sinSesion = false, mensaje = null)
        }
        viewModelScope.launch {
            val result = safeApiCall {
                val lista = mutableListOf<MovimientoInventario>()
                var pagina = 0
                var hayMas = true
                while (hayMas) {
                    val respuesta = api.movimientos(negocio.negocioId, page = pagina, size = 100)
                    lista += respuesta.content.map { dto ->
                        MovimientoInventario(
                            id = dto.id,
                            negocioId = negocio.negocioId,
                            productoNombre = dto.productoNombre,
                            usuarioNombre = dto.usuarioNombre,
                            tipo = TipoMovimiento.desdeClave(dto.tipo),
                            cantidad = dto.cantidad.toFloat(),
                            fecha = dto.createdAt,
                            referenciaOrigen = dto.referenciaTipo?.let { tipo ->
                                "$tipo #${dto.referenciaId}"
                            },
                            motivo = dto.motivo,
                            stockAnterior = dto.stockAnterior.toFloat(),
                            stockResultante = dto.stockResultante.toFloat()
                        )
                    }
                    hayMas = !respuesta.last
                    pagina += 1
                }
                lista
            }
            when (result) {
                is Resource.Success -> _uiState.update {
                    it.copy(movimientos = result.data, cargando = false)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(cargando = false, mensaje = result.message)
                }
            }
        }
    }

    fun cambiarPestana(indice: Int) {
        _uiState.update { it.copy(pestanaSeleccionada = indice.coerceIn(0, 3)) }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update { it.copy(busquedaTexto = texto) }
    }

    fun mensajeMostrado() {
        _uiState.update { it.copy(mensaje = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MovimientosViewModel() }
        }
    }
}
