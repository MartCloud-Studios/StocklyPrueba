package co.edu.univalle.stocklyapp.features.ventas.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.univalle.stocklyapp.features.ventas.domain.model.MetodoPago
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.ventas.domain.model.Venta
import co.edu.univalle.stocklyapp.app.navigation.BarraNavegacionInferior
import co.edu.univalle.stocklyapp.core.ui.components.BotonCancelar
import co.edu.univalle.stocklyapp.core.ui.components.BotonConfirmar
import co.edu.univalle.stocklyapp.core.ui.components.EncabezadoModulo
import co.edu.univalle.stocklyapp.app.navigation.Routes
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme

@Composable
fun VentasScreen(
    onNavegar: (String) -> Unit,
    viewModel: VentasViewModel = viewModel(factory = VentasViewModel.Factory)
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
        bottomBar = { BarraNavegacionInferior(Routes.VENTAS, onNavegar) },
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
                    titulo = "Ventas",
                    subtitulo = state.nombreNegocio,
                    icono = Icons.Default.PointOfSale
                )
            }

            if (state.puedeRegistrarVentas) {
                item {
                    FormularioNuevaVenta(state = state, viewModel = viewModel)
                }
            } else {
                item { Text("No tienes permiso para registrar ventas.") }
            }

            // Historial de Ventas
            item {
                Text(
                    text = "Historial de Ventas Realizadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(state.ventasHistorial, key = { it.id }) { venta ->
                TarjetaHistorialVenta(venta = venta)
            }
        }
    }

    // Diálogo Modal de Factura / Comprobante de Venta
    state.comprobanteGenerado?.let { venta ->
        DialogoComprobanteFactura(
            venta = venta,
            nombreNegocio = state.nombreNegocio,
            onCerrar = viewModel::cerrarComprobante
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioNuevaVenta(
    state: VentasUiState,
    viewModel: VentasViewModel
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Registrar Nueva Venta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ColoresApp.VerdeFondoOscuro
            )

            HorizontalDivider()

            // Agregar Productos al Carrito
            Text("Añadir Productos al Carrito:", fontWeight = FontWeight.Bold)
            SeccionAgregarProductoVenta(
                productos = state.productosDisponibles,
                onAgregar = viewModel::agregarProductoACarrito
            )

            // Carrito de Compras
            if (state.itemsCarrito.isNotEmpty()) {
                HorizontalDivider()
                Text("Carrito de Venta:", fontWeight = FontWeight.Bold)

                state.itemsCarrito.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.producto.nombre, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Precio Unit: $${String.format("%,.0f", item.precioUnitario)} • Stock: ${item.producto.cantidadDisponible}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresApp.TextoSecundario
                            )
                        }

                        // Controles - / +
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.cambiarCantidadCarrito(idx, item.cantidad - 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = null)
                            }

                            Text(
                                "${item.cantidad.toInt()}",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = { viewModel.cambiarCantidadCarrito(idx, item.cantidad + 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        Text(
                            "$${String.format("%,.0f", item.subtotal)}",
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { viewModel.eliminarItemCarrito(idx) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ColoresApp.Error)
                        }
                    }
                }

                HorizontalDivider()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { viewModel.procesarVenta(MetodoPago.EFECTIVO) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ColoresApp.VerdeBoton)
                    ) {
                        Text("Efectivo")
                    }
                    Button(
                        onClick = { viewModel.procesarVenta(MetodoPago.TARJETA) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ColoresApp.AzulEnlace)
                    ) {
                        Text("Tarjeta")
                    }
                }

                // Totales
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:", color = ColoresApp.TextoSecundario)
                        Text("$${String.format("%,.0f", state.subtotalCarrito)}")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL A COBRAR:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "$${String.format("%,.0f", state.totalVenta)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = ColoresApp.VerdeFondoOscuro
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionAgregarProductoVenta(
    productos: List<Producto>,
    onAgregar: (Producto, Float) -> Unit
) {
    var prodSel by remember { mutableStateOf<Producto?>(null) }
    var cantidadText by remember { mutableStateOf("1") }
    var expProd by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expProd,
            onExpandedChange = { expProd = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = prodSel?.nombre ?: "Seleccionar Producto",
                onValueChange = {},
                readOnly = true,
                label = { Text("Producto") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expProd) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expProd,
                onDismissRequest = { expProd = false }
            ) {
                productos.forEach { p ->
                    DropdownMenuItem(
                        text = { Text("${p.nombre} (Stock: ${p.cantidadDisponible} - $${String.format("%,.0f", p.precioVenta)})") },
                        onClick = {
                            prodSel = p
                            expProd = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = cantidadText,
            onValueChange = { cantidadText = it },
            label = { Text("Cant.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(80.dp)
        )

        IconButton(
            onClick = {
                prodSel?.let { p ->
                    val cant = cantidadText.toFloatOrNull() ?: 0f
                    onAgregar(p, cant)
                }
            },
            enabled = prodSel != null
        ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = "Añadir", tint = ColoresApp.VerdeBoton)
        }
    }
}

@Composable
private fun TarjetaHistorialVenta(venta: Venta) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Venta: ${venta.numeroVenta}",
                    fontWeight = FontWeight.Bold,
                    color = ColoresApp.VerdeFondoOscuro
                )
                Text(
                    text = "$${String.format("%,.0f", venta.totalVenta)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Text(
                text = "Pago: ${venta.metodoPago.etiqueta}",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresApp.TextoSecundario
            )
            Text(
                text = "Vendedor: ${venta.usuarioNombre} • ${venta.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresApp.TextoSecundario
            )

            Spacer(Modifier.height(8.dp))

            venta.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("• ${item.producto.nombre} x${item.cantidad}")
                        Text(
                            "Costo unitario: $${String.format("%,.2f", item.costoUnitario)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColoresApp.TextoSecundario
                        )
                    }
                    Text("$${String.format("%,.2f", item.subtotal)}")
                }
            }
        }
    }
}

@Composable
private fun DialogoComprobanteFactura(
    venta: Venta,
    nombreNegocio: String,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = ColoresApp.VerdeBoton)
                Spacer(Modifier.width(8.dp))
                Text("Comprobante de Venta")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(nombreNegocio, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Factura / Comprobante No: ${venta.numeroVenta}")
                Text("Fecha: ${venta.fecha}")
                Text("Atendido por: ${venta.usuarioNombre}")

                HorizontalDivider()

                venta.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.cantidad} x ${item.producto.nombre}")
                        Text("$${String.format("%,.2f", item.subtotal)}")
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Método Pago:", fontWeight = FontWeight.SemiBold)
                    Text(venta.metodoPago.etiqueta)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "$${String.format("%,.0f", venta.totalVenta)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColoresApp.VerdeFondoOscuro
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onCerrar) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cerrar")
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VentasScreenPreview() {
    StocklyAppTheme {
        VentasScreen(
            onNavegar = {}
        )
    }
}