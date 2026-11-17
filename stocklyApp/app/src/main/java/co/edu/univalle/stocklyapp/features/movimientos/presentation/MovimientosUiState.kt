package co.edu.univalle.stocklyapp.features.movimientos.presentation

import co.edu.univalle.stocklyapp.features.movimientos.domain.model.MovimientoInventario
import co.edu.univalle.stocklyapp.features.movimientos.domain.model.TipoMovimiento

data class MovimientosUiState(
    val nombreNegocio: String = "",
    val pestanaSeleccionada: Int = 0,
    val movimientos: List<MovimientoInventario> = emptyList(),
    val busquedaTexto: String = "",
    val cargando: Boolean = true,
    val mensaje: String? = null,
    val sinSesion: Boolean = false
) {
    val movimientosFiltrados: List<MovimientoInventario>
        get() = movimientos.filter { movimiento ->
            val coincideTipo = when (pestanaSeleccionada) {
                1 -> movimiento.tipo == TipoMovimiento.ENTRADA
                2 -> movimiento.tipo == TipoMovimiento.SALIDA
                3 -> movimiento.tipo == TipoMovimiento.AJUSTE
                else -> true
            }
            val coincideTexto = busquedaTexto.isBlank() ||
                movimiento.productoNombre.contains(busquedaTexto, ignoreCase = true) ||
                movimiento.usuarioNombre.contains(busquedaTexto, ignoreCase = true) ||
                movimiento.referenciaOrigen?.contains(busquedaTexto, ignoreCase = true) == true ||
                movimiento.motivo?.contains(busquedaTexto, ignoreCase = true) == true
            coincideTipo && coincideTexto
        }
}
