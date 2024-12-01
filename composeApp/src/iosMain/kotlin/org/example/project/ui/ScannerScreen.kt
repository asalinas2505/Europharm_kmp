package org.example.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun ScannerScreen() {
    val scannedCode = remember { "Feature no implementada en iOS por ahora." }

    Column(Modifier.fillMaxSize()) {
        // Parte superior: Vista de la cámara
        Box(
            Modifier
                .weight(3f)
                .fillMaxWidth()
        ) {
            Text(text = "Vista previa de cámara no disponible en iOS")
        }

        // Parte inferior: Resultado del escaneo
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Código Escaneado: $scannedCode")
        }
    }
}
