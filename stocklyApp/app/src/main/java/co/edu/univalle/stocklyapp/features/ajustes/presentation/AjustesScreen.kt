package co.edu.univalle.stocklyapp.features.ajustes.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.univalle.stocklyapp.features.inicio.domain.model.EstadisticasInicio
import co.edu.univalle.stocklyapp.core.session.model.NegocioAcceso
import co.edu.univalle.stocklyapp.app.navigation.BarraNavegacionInferior
import co.edu.univalle.stocklyapp.core.ui.components.BotonCancelar
import co.edu.univalle.stocklyapp.core.ui.components.BotonConfirmar
import co.edu.univalle.stocklyapp.core.ui.components.EncabezadoModulo
import co.edu.univalle.stocklyapp.app.navigation.Routes
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp

@Composable
fun AjustesScreen(
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: AjustesViewModel = viewModel(factory = AjustesViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.sinSesion) {
        if (state.sinSesion) onCerrarSesion()
    }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        bottomBar = { BarraNavegacionInferior(Routes.AJUSTES, onNavegar) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EncabezadoModulo(
                    titulo = "Ajustes y reportes",
                    subtitulo = "Cuenta y estadísticas del negocio",
                    icono = Icons.Default.Settings
                )
            }

            // Perfil de Usuario
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ColoresApp.AzulClaro,
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ColoresApp.AzulEnlace,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Column {
                            Text(
                                text = state.nombreUsuario.ifBlank { "Usuario" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.correoUsuario,
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresApp.TextoSecundario
                            )
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                color = ColoresApp.VerdeBoton.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Negocio: ${state.nombreNegocio} (${state.rolActual.uppercase()})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColoresApp.VerdeFondoOscuro,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Opciones de Configuración
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        OpcionMenuAjustes(
                            icono = Icons.Default.Business,
                            titulo = "Cambiar de Negocio o Rol",
                            subtitulo = "Accede a tus otros negocios asignados",
                            onClick = viewModel::abrirCambiarNegocio
                        )
                    }
                }
            }

            // GRÁFICAS DE EVOLUCIÓN Y ALERTAS DE STOCK (En Ajustes para Propietarios y todos los roles)
            state.estadisticasDashboard?.let { stats ->
                item {
                    Text(
                        text = "Reportes y Gráficas de Stock",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    SeccionDashboardStockAjustes(stats = stats)
                }
            }

            // Botón Cerrar Sesión
            item {
                OutlinedButton(
                    onClick = viewModel::abrirConfirmarCerrarSesion,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ColoresApp.Error)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = ColoresApp.Error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("CERRAR SESIÓN", color = ColoresApp.Error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (state.mostrandoCambiarNegocio) {
        DialogoSeleccionNegocio(
            negocios = state.negociosAcceso,
            onCancelar = viewModel::cerrarCambiarNegocio,
            onSeleccionar = viewModel::seleccionarNegocio
        )
    }

    if (state.mostrandoConfirmarCerrarSesion) {
        AlertDialog(
            onDismissRequest = viewModel::cerrarConfirmarCerrarSesion,
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tendrás que ingresar nuevamente tus credenciales para acceder a tus negocios.") },
            confirmButton = {
                BotonConfirmar("Aceptar", onClick = viewModel::cerrarSesion)
            },
            dismissButton = {
                BotonCancelar("Cancelar", onClick = viewModel::cerrarConfirmarCerrarSesion)
            }
        )
    }
}

@Composable
private fun OpcionMenuAjustes(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = ColoresApp.AzulEnlace)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontWeight = FontWeight.Bold)
            Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = ColoresApp.TextoSecundario)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ColoresApp.TextoSecundario)
    }
}

@Composable
private fun SeccionDashboardStockAjustes(stats: EstadisticasInicio) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Card 1: Alertas de Stock Bajo
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = ColoresApp.Error)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Alertas de Stock Bajo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ColoresApp.Error
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (stats.alertasBajoStock.isEmpty()) {
                    Text("No hay productos con stock bajo.", color = ColoresApp.TextoSecundario)
                } else {
                    stats.alertasBajoStock.forEach { alerta ->
                        val proporcion = (alerta.cantidadActual / alerta.cantidadMinima).coerceIn(0f, 1f)
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(alerta.productoNombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    "${alerta.cantidadActual} / Mín: ${alerta.cantidadMinima} ${alerta.unidad}",
                                    fontWeight = FontWeight.Bold,
                                    color = ColoresApp.Error,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { proporcion },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ColoresApp.Error,
                                trackColor = ColoresApp.Error.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }

        // Card 2: Evolución de Stock
        if (stats.evolucionStock.isNotEmpty()) Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = ColoresApp.AzulEnlace)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Evolución de Stock (Últimos días)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    stats.evolucionStock.forEach { p ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val altura = (p.stockTotal / 200f * 80).dp
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(altura)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(ColoresApp.AzulEnlace)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(p.dia, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Card 3: Top Productos
        var verMasVendidos by remember { mutableStateOf(true) }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (verMasVendidos) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (verMasVendidos) ColoresApp.VerdeFondoOscuro else Color(0xFFD97706)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (verMasVendidos) "Más vendidos recientemente" else "Menos vendidos recientemente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = { verMasVendidos = !verMasVendidos }) {
                        Text(if (verMasVendidos) "Ver Menos" else "Ver Más", fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))

                val listaTop = if (verMasVendidos) stats.topMasVendidos else stats.topMenosVendidos
                if (listaTop.isEmpty()) {
                    Text("No hay ventas registradas recientemente.", color = ColoresApp.TextoSecundario)
                } else listaTop.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${idx + 1}.", fontWeight = FontWeight.Bold, color = ColoresApp.TextoSecundario)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(item.productoNombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    "${item.cantidadVendida} ${item.unidad} vendidas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColoresApp.TextoSecundario
                                )
                            }
                        }

                        Text(
                            "$${String.format("%,.0f", item.totalGenerado)}",
                            fontWeight = FontWeight.Bold,
                            color = ColoresApp.VerdeFondoOscuro
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoSeleccionNegocio(
    negocios: List<NegocioAcceso>,
    onCancelar: () -> Unit,
    onSeleccionar: (NegocioAcceso) -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Seleccionar Negocio Activo") },
        text = {
            Column {
                if (negocios.isEmpty()) {
                    Text("No tienes negocios asignados.")
                } else {
                    negocios.forEachIndexed { idx, neg ->
                        if (idx > 0) HorizontalDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSeleccionar(neg) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(neg.nombre, fontWeight = FontWeight.Bold)
                                Text(
                                    "Rol: ${neg.rol.lowercase().replace('_', ' ')}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ColoresApp.TextoSecundario
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ColoresApp.AzulEnlace)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { BotonCancelar("Cerrar", onClick = onCancelar) }
    )
}

@Preview(
    name = "Ajustes - Pantalla completa",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AjustesScreenPreview() {
    AjustesScreen(
        onNavegar = {},
        onCerrarSesion = {}
    )
}