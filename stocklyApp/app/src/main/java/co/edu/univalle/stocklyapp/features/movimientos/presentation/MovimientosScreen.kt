package co.edu.univalle.stocklyapp.features.movimientos.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.univalle.stocklyapp.features.movimientos.domain.model.MovimientoInventario
import co.edu.univalle.stocklyapp.features.movimientos.domain.model.TipoMovimiento
import co.edu.univalle.stocklyapp.app.navigation.BarraNavegacionInferior
import co.edu.univalle.stocklyapp.app.navigation.Routes
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme
import co.edu.univalle.stocklyapp.core.ui.components.EncabezadoModulo

@Composable
fun MovimientosScreen(
    onNavegar: (String) -> Unit,
    viewModel: MovimientosViewModel = viewModel(factory = MovimientosViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        bottomBar = { BarraNavegacionInferior(Routes.MOVIMIENTOS, onNavegar) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            EncabezadoModulo(
                titulo = "Movimientos de inventario",
                subtitulo = state.nombreNegocio,
                icono = Icons.Default.SwapHoriz,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            TabRow(
                selectedTabIndex = state.pestanaSeleccionada,
                containerColor = Color.White,
                contentColor = ColoresApp.AzulEnlace,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.pestanaSeleccionada]),
                        color = ColoresApp.AzulEnlace
                    )
                }
            ) {
                listOf("Todos", "Entradas", "Salidas", "Ajustes").forEachIndexed { index, label ->
                    Tab(
                        selected = state.pestanaSeleccionada == index,
                        onClick = { viewModel.cambiarPestana(index) },
                        text = {
                            Text(
                                label,
                                fontWeight = if (state.pestanaSeleccionada == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Buscador
            OutlinedTextField(
                value = state.busquedaTexto,
                onValueChange = viewModel::onBusquedaChange,
                placeholder = { Text("Buscar producto, usuario o ref...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            val lista = state.movimientosFiltrados
            if (lista.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay registros de movimientos en esta sección",
                        color = ColoresApp.TextoSecundario
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(lista, key = { it.id }) { mov ->
                        TarjetaMovimiento(movimiento = mov)
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaMovimiento(movimiento: MovimientoInventario) {
    val esSalida = movimiento.tipo == TipoMovimiento.SALIDA
    val colorBadge = if (esSalida) ColoresApp.Error else ColoresApp.AzulEnlace
    val iconoBadge = if (esSalida) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorBadge.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(iconoBadge, contentDescription = null, tint = colorBadge)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = movimiento.productoNombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(movimiento.tipo.etiqueta, fontWeight = FontWeight.Bold, color = colorBadge)
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "Cantidad: ${movimiento.cantidad} • Stock: ${movimiento.stockAnterior} → ${movimiento.stockResultante}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColoresApp.TextoSecundario
                )

                Text(
                    text = listOfNotNull(
                        "Por: ${movimiento.usuarioNombre}",
                        movimiento.referenciaOrigen,
                        movimiento.motivo?.let { "Motivo: $it" }
                    ).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = ColoresApp.TextoSecundario
                )

                Text(
                    text = movimiento.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MovimientosScreenPreview() {
    StocklyAppTheme {
        MovimientosScreen(
            onNavegar = {}
        )
    }
}