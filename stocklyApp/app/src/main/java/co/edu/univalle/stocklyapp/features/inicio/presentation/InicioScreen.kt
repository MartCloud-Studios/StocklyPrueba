package co.edu.univalle.stocklyapp.features.inicio.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.univalle.stocklyapp.features.inicio.domain.model.EstadisticasInicio
import co.edu.univalle.stocklyapp.features.negocio.domain.model.Rol
import co.edu.univalle.stocklyapp.features.negocio.domain.model.UsuarioNegocio
import co.edu.univalle.stocklyapp.app.navigation.BarraNavegacionInferior
import co.edu.univalle.stocklyapp.app.navigation.Routes
import co.edu.univalle.stocklyapp.core.ui.components.EncabezadoModulo
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme
import kotlinx.coroutines.launch

@Composable
fun InicioScreen(
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: InicioViewModel = viewModel(factory = InicioViewModel.Factory),
    previewState: InicioUiState? = null
) {
    val stateReal by viewModel.uiState.collectAsStateWithLifecycle()
    val state = previewState ?: stateReal
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.sinSesion) {
        if (state.sinSesion) onCerrarSesion()
    }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    DialogosInicio(
        state = state,
        viewModel = viewModel,
        onCerrarSesionConfirmado = viewModel::cerrarSesion
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                state = state,
                onCerrarSesion = {
                    scope.launch {
                        drawerState.close()
                        viewModel.confirmarCerrarSesion()
                    }
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = { BarraNavegacionInferior(Routes.INICIO, onNavegar) }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                EncabezadoModulo(
                    titulo = "Inicio",
                    subtitulo = "${state.rolActual} · ${state.nombreNegocio}",
                    icono = Icons.Default.Home,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    contenidoFinal = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color.White)
                        }
                        IconButton(onClick = viewModel::confirmarCerrarSesion) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = Color.White
                            )
                        }
                    }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OutlinedButton(
                            onClick = viewModel::abrirOtrosRoles,
                            enabled = !state.ocupado,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, ColoresApp.VerdeFondo),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = ColoresApp.VerdeFondo,
                                contentColor = ColoresApp.TextoBlanco
                            )
                        ) {
                            Text(
                                "CAMBIAR DE ROL Y/O NEGOCIO",
                                style = MaterialTheme.typography.labelLarge,
                                color = ColoresApp.TextoBlanco,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // VISTA PARA PROPIETARIOS: Añadir usuarios y lista de colaboradores
                    if (state.puedeAdministrarUsuarios) {
                        item {
                            SeccionAnadirUsuario(
                                correo = state.correoBusqueda,
                                error = state.errorBusqueda,
                                onCorreoChange = viewModel::onCorreoBusquedaChange,
                                onAnadir = viewModel::buscar
                            )
                        }

                        item {
                            Text(
                                text = "Colaboradores añadidos a tu empresa:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        when {
                            state.cargandoLista -> item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = ColoresApp.AzulEnlace)
                                }
                            }

                            state.errorLista != null -> item {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = state.errorLista!!,
                                        color = ColoresApp.Error,
                                        textAlign = TextAlign.Center
                                    )
                                    TextButton(onClick = { viewModel.cargarUsuarios() }) {
                                        Text("Reintentar", color = ColoresApp.AzulEnlace)
                                    }
                                }
                            }

                            state.usuariosNegocio.isEmpty() -> item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Aún no has añadido usuarios a tu negocio",
                                        color = ColoresApp.TextoSecundario,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            else -> items(state.usuariosNegocio, key = { it.id }) { usuario ->
                                Column {
                                    FilaUsuario(
                                        usuario = usuario,
                                        enabled = !state.ocupado,
                                        onRolClick = { viewModel.abrirCambiarRol(usuario) }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    } else {
                        // VISTA DE DASHBOARD PARA ALMACENISTAS Y VENDEDORES
                        state.estadisticasDashboard?.let { stats ->
                            item {
                                SeccionDashboardStock(stats = stats)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// DASHBOARD DE STOCK (ALERTAS, EVOLUCIÓN Y TOP PRODUCTOS)
// ---------------------------------------------------------------------------

@Composable
private fun SeccionDashboardStock(stats: EstadisticasInicio) {
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

// ---------------------------------------------------------------------------
// TARJETA DE CABECERA CON EL BOTÓN DE CERRAR SESIÓN
// ---------------------------------------------------------------------------

@Composable
private fun SeccionAnadirUsuario(
    correo: String,
    error: String?,
    onCorreoChange: (String) -> Unit,
    onAnadir: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Añade un usuario a tu negocio:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = correo,
                onValueChange = onCorreoChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("ej: correo@gmail.com") },
                singleLine = true,
                isError = error != null,
                supportingText = error?.let { { Text(it) } },
                shape = RoundedCornerShape(6.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onAnadir() })
            )
            Button(
                onClick = onAnadir,
                modifier = Modifier
                    .height(56.dp)
                    .width(88.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColoresApp.VerdeFondo),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Añadir", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FilaUsuario(
    usuario: UsuarioNegocio,
    enabled: Boolean,
    onRolClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarInicial(nombre = usuario.nombre)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = usuario.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = usuario.correo,
                style = MaterialTheme.typography.bodySmall,
                color = ColoresApp.TextoSecundario,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        OutlinedButton(
            onClick = onRolClick,
            enabled = enabled,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, ColoresApp.AzulEnlace),
            contentPadding = PaddingValues(start = 12.dp, end = 4.dp)
        ) {
            Text(usuario.rol.etiqueta.lowercase())
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Cambiar rol")
        }
    }
}

@Composable
private fun AvatarInicial(nombre: String, tamano: Int = 40) {
    val inicial = nombre.trim().firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .clip(CircleShape)
            .background(ColoresApp.AzulClaro)
            .border(1.dp, ColoresApp.AzulEnlace, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = inicial,
            color = ColoresApp.AzulEnlace,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MenuLateral(
    state: InicioUiState,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(24.dp)) {
            AvatarInicial(nombre = state.nombreUsuario, tamano = 56)
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.nombreUsuario,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = state.correoUsuario,
                style = MaterialTheme.typography.bodyMedium,
                color = ColoresApp.TextoSecundario
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Negocio: ${state.nombreNegocio}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        NavigationDrawerItem(
            label = { Text("Cerrar sesión", color = ColoresApp.Error) },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = ColoresApp.Error
                )
            },
            selected = false,
            onClick = onCerrarSesion,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Vista previa - Inicio usuario"
)
@Composable
private fun InicioScreenPreview() {
    StocklyAppTheme {
        InicioScreen(
            onNavegar = {},
            onCerrarSesion = {},
            previewState = InicioUiState(
                nombreUsuario = "Oscar Martínez",
                correoUsuario = "oscar@univalle.edu.co",
                nombreNegocio = "Stockly Store",
                rolActual = "propietario",
                puedeAdministrarUsuarios = true,
                cargandoLista = false,
                usuariosNegocio = listOf(
                    UsuarioNegocio(
                        id = 1L,
                        nombre = "María Fernanda",
                        correo = "maria@gmail.com",
                        rol = Rol.ALMACENISTA
                    ),
                    UsuarioNegocio(
                        id = 2L,
                        nombre = "Carlos Andrés",
                        correo = "carlos@gmail.com",
                        rol = Rol.VENDEDOR
                    ),
                    UsuarioNegocio(
                        id = 3L,
                        nombre = "Laura Martínez",
                        correo = "laura@gmail.com",
                        rol = Rol.ALMACENISTA
                    ),
                    UsuarioNegocio(
                        id = 4L,
                        nombre = "Andrés Felipe",
                        correo = "andres@gmail.com",
                        rol = Rol.VENDEDOR
                    )
                )
            )
        )
    }
}