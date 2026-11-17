package co.edu.univalle.stocklyapp.features.movimientos.domain.model

enum class TipoMovimiento(val etiqueta: String, val esEntrada: Boolean) {
    ENTRADA("Entrada", true),
    SALIDA("Salida", false),
    AJUSTE("Ajuste", true);

    companion object {
        fun desdeClave(clave: String): TipoMovimiento =
            entries.firstOrNull { it.name.equals(clave, ignoreCase = true) } ?: AJUSTE
    }
}

/** Registro en el historial unificado de movimientos. */
data class MovimientoInventario(
    val id: Long,
    val negocioId: Long,
    val productoNombre: String,
    val usuarioNombre: String,
    val tipo: TipoMovimiento,
    val cantidad: Float,
    val fecha: String,
    val referenciaOrigen: String? = null,
    val motivo: String? = null,
    val stockAnterior: Float = 0f,
    val stockResultante: Float = 0f
)
