package co.edu.univalle.stocklyapp.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme
import co.edu.univalle.stocklyapp.features.ajustes.presentation.AjustesScreen
import co.edu.univalle.stocklyapp.features.compras.presentation.ComprasScreen
import co.edu.univalle.stocklyapp.features.auth.presentation.contrasena.ContrasenaScreen
import co.edu.univalle.stocklyapp.features.inicio.presentation.InicioScreen
import co.edu.univalle.stocklyapp.features.inventario.presentation.InventarioScreen
import co.edu.univalle.stocklyapp.features.auth.presentation.login.LoginScreen
import co.edu.univalle.stocklyapp.app.presentation.main.MainScreen
import co.edu.univalle.stocklyapp.features.movimientos.presentation.MovimientosScreen
import co.edu.univalle.stocklyapp.features.auth.presentation.registro.RegistroScreen
import co.edu.univalle.stocklyapp.features.ventas.presentation.VentasScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {

    val navegarInferior: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Routes.INICIO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val cerrarSesion: () -> Unit = {
        navController.navigate(Routes.MAIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Routes.MAIN) {

        composable(Routes.MAIN) {
            MainScreen(
                onCrearCuenta = { navController.navigate(Routes.REGISTRO) },
                onIniciarSesion = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTRO) { inclusive = true }
                    }
                },
                onIrALogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTRO) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Routes.INICIO) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate(Routes.REGISTRO) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onOlvideContrasena = { navController.navigate(Routes.CONTRASENA) }
            )
        }

        composable(Routes.CONTRASENA) {
            ContrasenaScreen(onVolver = { navController.popBackStack() })
        }

        composable(Routes.INICIO) {
            InicioScreen(
                onNavegar = navegarInferior,
                onCerrarSesion = cerrarSesion
            )
        }

        composable(Routes.INVENTARIO) {
            InventarioScreen(
                onNavegar = navegarInferior,
                onIrACompras = { navController.navigate(Routes.COMPRAS) },
                onIrAVentas = { navController.navigate(Routes.VENTAS) }
            )
        }

        composable(Routes.COMPRAS) {
            ComprasScreen(
                onNavegar = navegarInferior
            )
        }

        composable(Routes.VENTAS) {
            VentasScreen(
                onNavegar = navegarInferior
            )
        }

        composable(Routes.MOVIMIENTOS) {
            MovimientosScreen(
                onNavegar = navegarInferior
            )
        }

        composable(Routes.AJUSTES) {
            AjustesScreen(
                onNavegar = navegarInferior,
                onCerrarSesion = cerrarSesion
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppNavigationPreview() {
    StocklyAppTheme {
        AppNavigation()
    }
}
