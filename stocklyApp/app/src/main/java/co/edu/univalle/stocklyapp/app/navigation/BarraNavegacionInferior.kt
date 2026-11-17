package co.edu.univalle.stocklyapp.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme
import co.edu.univalle.stocklyapp.core.session.PermisosNegocio
import co.edu.univalle.stocklyapp.core.session.SesionManager
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingCart

enum class DestinoInferior(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    INICIO(Routes.INICIO, "Inicio", Icons.Default.Home),
    INVENTARIO(Routes.INVENTARIO, "Inventario", Icons.Default.Inventory2),
    MOVIMIENTOS(Routes.MOVIMIENTOS, "Movimientos", Icons.Default.SwapHoriz),
    COMPRAS(Routes.COMPRAS, "Compras", Icons.Default.ShoppingCart),
    VENTAS(Routes.VENTAS, "Ventas", Icons.Default.PointOfSale),
    AJUSTES(Routes.AJUSTES, "Ajustes", Icons.Default.Settings)
}

@Composable
fun BarraNavegacionInferior(
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    val usuario by SesionManager.usuario.collectAsState()
    val rol = usuario?.negocioActual?.rol
    val destinos = DestinoInferior.entries.filter { destino ->
        when (destino) {
            DestinoInferior.INICIO, DestinoInferior.AJUSTES -> true
            DestinoInferior.INVENTARIO, DestinoInferior.MOVIMIENTOS ->
                PermisosNegocio.puedeConsultar(rol)
            DestinoInferior.COMPRAS -> PermisosNegocio.puedeConsultarCompras(rol)
            DestinoInferior.VENTAS -> PermisosNegocio.puedeRegistrarVentas(rol)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = ColoresApp.FondoBlanco,
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            NavigationBar(
                containerColor = ColoresApp.FondoBlanco,
                tonalElevation = 0.dp
            ) {
                destinos.forEach { destino ->
                    NavigationBarItem(
                        selected = destino.ruta == rutaActual,
                        onClick = { onNavegar(destino.ruta) },
                        icon = {
                            Icon(
                                imageVector = destino.icono,
                                contentDescription = destino.etiqueta
                            )
                        },
                        label = {
                            Text(destino.etiqueta)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColoresApp.AzulEnlace,
                            selectedTextColor = ColoresApp.AzulEnlace,
                            indicatorColor = ColoresApp.AzulClaro,
                            unselectedIconColor = ColoresApp.VerdeFondo,
                            unselectedTextColor = ColoresApp.VerdeFondoOscuro
                        )
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Preview - Barra inferior"
)
@Composable
fun BarraNavegacionInferiorPreview() {
    StocklyAppTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ColoresApp.FondoBlanco
        ) {
            BarraNavegacionInferior(
                rutaActual = Routes.INICIO,
                onNavegar = {}
            )
        }
    }
}