package co.edu.univalle.stocklyapp.app.presentation.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.univalle.stocklyapp.R
import co.edu.univalle.stocklyapp.core.ui.components.BotonVerde
import co.edu.univalle.stocklyapp.features.auth.presentation.contrasena.ContrasenaScreen
import co.edu.univalle.stocklyapp.core.ui.theme.ColoresApp
import co.edu.univalle.stocklyapp.core.ui.theme.material.StocklyAppTheme

@Composable
fun MainScreen(
    onCrearCuenta: () -> Unit,
    onIniciarSesion: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // Parte superior: fondo verde con la imagen de la app (res/drawable/stocly.jpg)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ColoresApp.AzulFondo),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.stockly),
                contentDescription = "Logo de Stockly",
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(40.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Stockly",
                    fontSize = 50.sp,
                    modifier = Modifier
                        .background(ColoresApp.AzulFondo)
                        .padding(bottom = 70.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ColoresApp.TextoBlanco
                )
            }

        }

        // Parte inferior: botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BotonVerde(
                texto = "Crear cuenta",
                onClick = onCrearCuenta,
                colorFondo = ColoresApp.VerdeBoton
            )

            OutlinedButton(
                onClick = onIniciarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, ColoresApp.VerdeBoton),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ColoresApp.VerdeBoton,
                    containerColor = Color.Transparent
                )
            ) {
                Text("INICIAR SESIÓN")
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Vista previa - Pantalla principal"
)
@Composable
private fun MainScreenPreview() {
    StocklyAppTheme {
        MainScreen(
            onCrearCuenta = {},
            onIniciarSesion = {}
        )
    }
}