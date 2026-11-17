package co.edu.univalle.stocklyapp.features.negocio.domain.model

enum class Rol(val clave: String, val etiqueta: String, val descripcion: String) {
    ALMACENISTA(
        "ALMACENISTA",
        "Almacenista",
        "El almacenista es quien suministra los productos de tu negocio"
    ),
    VENDEDOR(
        "VENDEDOR",
        "Vendedor",
        "El vendedor es quien vende los productos de tu negocio"
    );

    companion object {
        fun desdeClave(clave: String?): Rol? =
            entries.firstOrNull { it.clave.equals(clave, ignoreCase = true) }
    }
}
