package co.edu.univalle.stocklyapp.features.compras.presentation

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import co.edu.univalle.stocklyapp.features.compras.domain.model.Compra
import co.edu.univalle.stocklyapp.features.compras.domain.model.EstadoCompra
import co.edu.univalle.stocklyapp.features.inventario.domain.model.Producto
import co.edu.univalle.stocklyapp.features.compras.domain.model.Proveedor
import co.edu.univalle.stocklyapp.app.navigation.BarraNavegacionInferior
import co.edu.univalle.stocklyapp.core.ui.components.BotonCancelar
import co.edu.univalle.stocklyapp.core.ui.components.BotonConfirmar
import co.edu.univalle.stocklyapp.core.ui.components.EncabezadoModulo
import co.edu.univalle.stocklyapp.app.navigation.Routes
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme

@Composable
fun ComprasScreen(
    onNavegar: (String) -> Unit,
    viewModel: ComprasViewModel = viewModel(factory = ComprasViewModel.Factory)
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
        bottomBar = { BarraNavegacionInferior(Routes.COMPRAS, onNavegar) },
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
                    titulo = "Compras",
                    subtitulo = state.nombreNegocio,
                    icono = Icons.Default.LocalShipping
                )
            }

            if (state.puedeConsultar) {
                item {
                    FormularioNuevaCompra(state = state, viewModel = viewModel)
                }
            } else {
                item { Text("No tienes permiso para consultar compras.") }
            }

            // Historial de Compras
            item {
                Text(
                    text = "Historial de Compras / Reabastecimiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (state.comprasHistorial.isEmpty() && !state.cargando) {
                item { Text("No hay compras registradas.") }
            } else {
                items(state.comprasHistorial, key = { it.id }) { compra ->
                    TarjetaHistorialCompra(compra = compra)
                }
            }
        }
    }

    if (state.mostrandoCrearProveedor) {
        DialogoCrearProveedor(
            onCancelar = viewModel::cerrarCrearProveedor,
            onGuardar = viewModel::guardarProveedor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioNuevaCompra(
    state: ComprasUiState,
    viewModel: ComprasViewModel
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
                text = "Registrar Nueva Compra / Reabastecimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ColoresApp.AzulFondo
            )

            // Selector de Proveedor con botón para añadir
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var expProv by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expProv,
                    onExpandedChange = { expProv = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.proveedorSeleccionado?.nombre ?: "Seleccionar Proveedor",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Proveedor") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expProv) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expProv,
                        onDismissRequest = { expProv = false }
                    ) {
                        state.proveedores.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.nombre) },
                                onClick = {
                                    viewModel.seleccionarProveedor(p)
                                    expProv = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = viewModel::abrirCrearProveedor,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.AddBusiness, contentDescription = "Nuevo Proveedor", tint = ColoresApp.AzulEnlace)
                }
            }

            // Número de Factura Proveedor
            OutlinedTextField(
                value = state.numeroFactura,
                onValueChange = viewModel::onNumeroFacturaChange,
                label = { Text("No. Factura (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // Selector de Producto a Añadir
            Text("Añadir Productos a la Orden:", fontWeight = FontWeight.Bold)
            SeccionAgregarProductoAOrden(
                productos = state.productosDisponibles,
                onAgregar = viewModel::agregarProductoACompra
            )

            // Tabla de Items
            if (state.itemsCompra.isNotEmpty()) {
                HorizontalDivider()
                Text("Resumen de Orden:", fontWeight = FontWeight.Bold)
                state.itemsCompra.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.producto.nombre, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${item.cantidad} ${item.producto.unidadMedida.abreviatura} x $${String.format("%,.0f", item.costoUnitario)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColoresApp.TextoSecundario
                            )
                        }
                        Text(
                            "$${String.format("%,.0f", item.subtotal)}",
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { viewModel.eliminarItemCompra(idx) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ColoresApp.Error)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("COSTO TOTAL:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "$${String.format("%,.0f", state.totalCostoCompra)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColoresApp.AzulEnlace
                    )
                }

                Button(
                    onClick = viewModel::registrarCompra,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColoresApp.VerdeBoton)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("REGISTRAR COMPRA Y SUMAR STOCK")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionAgregarProductoAOrden(
    productos: List<Producto>,
    onAgregar: (Producto, Float, Double) -> Unit
) {
    var prodSel by remember { mutableStateOf<Producto?>(null) }
    var cantidadText by remember { mutableStateOf("10") }
    var costoText by remember { mutableStateOf("") }
    var expProd by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ExposedDropdownMenuBox(
            expanded = expProd,
            onExpandedChange = { expProd = it },
            modifier = Modifier.fillMaxWidth()
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
                        text = { Text("${p.nombre} (Stock: ${p.cantidadDisponible})") },
                        onClick = {
                            prodSel = p
                            costoText = p.costoActual.toString()
                            expProd = false
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = cantidadText,
                onValueChange = { cantidadText = it },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = costoText,
                onValueChange = { costoText = it },
                label = { Text("Costo Unit. ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                prodSel?.let { p ->
                    val cant = cantidadText.toFloatOrNull() ?: 0f
                    val cost = costoText.toDoubleOrNull() ?: -1.0
                    onAgregar(p, cant, cost)
                }
            },
            enabled = prodSel != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Añadir a la lista")
        }
    }
}

@Composable
private fun TarjetaHistorialCompra(compra: Compra) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Factura: ${compra.numeroFacturaProveedor}",
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = ColoresApp.VerdeClaro.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ColoresApp.VerdeFondoOscuro,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = compra.estado.etiqueta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColoresApp.VerdeFondoOscuro
                        )
                    }
                }
            }

            Text(
                text = "Proveedor: ${compra.proveedor?.nombre ?: "No especificado"}",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresApp.TextoSecundario
            )
            Text(
                text = "Registrado por: ${compra.usuarioNombre} • ${compra.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = ColoresApp.TextoSecundario
            )

            Spacer(Modifier.height(8.dp))

            compra.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("• ${item.producto.nombre} (${item.cantidad} ${item.producto.unidadMedida.abreviatura})")
                    Text("$${String.format("%,.0f", item.subtotal)}")
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TOTAL COMPRA:", fontWeight = FontWeight.Bold)
                Text(
                    "$${String.format("%,.0f", compra.totalCosto)}",
                    fontWeight = FontWeight.Bold,
                    color = ColoresApp.AzulEnlace
                )
            }
        }
    }
}

@Composable
private fun DialogoCrearProveedor(
    onCancelar: () -> Unit,
    onGuardar: (String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Añadir Nuevo Proveedor") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del proveedor") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            BotonConfirmar(
                texto = "Guardar",
                onClick = { onGuardar(nombre, telefono, email) }
            )
        },
        dismissButton = { BotonCancelar("Cancelar", onClick = onCancelar) }
    )
}

@Preview(
    name = "Compras - Pantalla completa",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComprasScreenPreview() {
    StocklyAppTheme {
        ComprasScreen(
            onNavegar = {}
        )
    }
}