package co.edu.univalle.stocklyapp.core.session

object PermisosNegocio {
    private const val PROPIETARIO = "PROPIETARIO"
    private const val ALMACENISTA = "ALMACENISTA"
    private const val VENDEDOR = "VENDEDOR"

    fun puedeConsultar(rol: String?): Boolean = rolClave(rol) in rolesValidos

    fun puedeGestionarCatalogo(rol: String?): Boolean =
        rolClave(rol) == PROPIETARIO || rolClave(rol) == ALMACENISTA

    fun puedeGestionarInventario(rol: String?): Boolean = puedeGestionarCatalogo(rol)

    fun puedeConsultarCompras(rol: String?): Boolean =
        rolClave(rol) == PROPIETARIO || rolClave(rol) == ALMACENISTA

    fun puedeRegistrarVentas(rol: String?): Boolean = rolClave(rol) in rolesValidos

    fun puedeConsultarTodasLasVentas(rol: String?): Boolean =
        rolClave(rol) == PROPIETARIO || rolClave(rol) == ALMACENISTA

    private val rolesValidos = setOf(PROPIETARIO, ALMACENISTA, VENDEDOR)

    private fun rolClave(rol: String?): String =
        rol?.trim()?.uppercase().orEmpty()
}
