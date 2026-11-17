package co.edu.univalle.stocklyapp.core.session

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermisosNegocioTest {
    @Test
    fun inventoryAndCatalogWritesAreLimitedToOwnerAndStorekeeper() {
        assertTrue(PermisosNegocio.puedeGestionarCatalogo("PROPIETARIO"))
        assertTrue(PermisosNegocio.puedeGestionarInventario("ALMACENISTA"))
        assertFalse(PermisosNegocio.puedeGestionarCatalogo("VENDEDOR"))
        assertFalse(PermisosNegocio.puedeGestionarInventario(null))
    }

    @Test
    fun purchaseAccessIsLimitedButSalesAreAvailableToAllBusinessRoles() {
        listOf("PROPIETARIO", "ALMACENISTA").forEach {
            assertTrue(PermisosNegocio.puedeConsultarCompras(it))
        }
        assertFalse(PermisosNegocio.puedeConsultarCompras("VENDEDOR"))
        listOf("PROPIETARIO", "ALMACENISTA", "VENDEDOR").forEach {
            assertTrue(PermisosNegocio.puedeConsultar(it))
            assertTrue(PermisosNegocio.puedeRegistrarVentas(it))
        }
        assertFalse(PermisosNegocio.puedeRegistrarVentas("INVITADO"))
    }
}
