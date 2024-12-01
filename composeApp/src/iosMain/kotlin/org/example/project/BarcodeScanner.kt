package org.example.project
/*z
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class BarcodeScanner actual constructor() {
    private val _scannedText = MutableStateFlow("Esperando escaneo...")
    actual val scannedText: StateFlow<String> = _scannedText

    actual fun startScanning() {
        // Simulación de escaneo: actualiza el flujo con un texto
        _scannedText.value = "Código escaneado: 12345"
    }

    actual fun stopScanning() {
        _scannedText.value = "Escaneo detenido"
    }
}
*/