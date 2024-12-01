// AndroidMain - ScannerScreen.kt

package org.example.project.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import org.example.project.scanner.AndroidScanner

@Composable
actual fun ScannerScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    var scannedCode by remember { mutableStateOf(" ") }
    val scanner = remember { AndroidScanner(context, lifecycleOwner) }

    Column(Modifier.fillMaxSize()) {
        // Parte superior: Vista de la cámara
        Box(
            Modifier
                .weight(4f)
                .fillMaxWidth()
        ) {
            CameraPreview(scanner)
        }

        // Parte inferior: Resultado del escaneo
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Código Escaneado: $scannedCode el coño de tu prima")
        }
    }
}
