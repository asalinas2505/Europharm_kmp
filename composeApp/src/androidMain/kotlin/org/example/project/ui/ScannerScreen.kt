// androidMain - ScannerScreen.kt
package org.example.project.ui

import android.util.Log
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
actual fun ScannerScreen() {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner

    var scannedResult by remember { mutableStateOf("Esperando escaneo...") }
    var debugMessage by remember { mutableStateOf("Cargando cámara...") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Cámara
        Box(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = androidx.camera.view.PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    initializeCamera(ctx, lifecycleOwner, previewView) { result ->
                        scannedResult = result
                        debugMessage = "Código escaneado: $result"
                    }
                    previewView
                }
            )
        }

        // Resultado
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = scannedResult)
                Text(text = debugMessage)
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun initializeCamera(
    context: android.content.Context,
    lifecycleOwner: LifecycleOwner,
    previewView: androidx.camera.view.PreviewView,
    onResult: (String) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraExecutor = Executors.newSingleThreadExecutor()

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val barcodeScanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_DATA_MATRIX
                    ).build()
            )

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                val rawValue = barcode.rawValue
                                if (!rawValue.isNullOrEmpty()) {
                                    onResult(rawValue)
                                    imageProxy.close()
                                    return@addOnSuccessListener
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.e("ScannerScreen", "Error escaneando: ${it.message}")
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e("ScannerScreen", "Error inicializando cámara: ${e.message}")
        }
    }, ContextCompat.getMainExecutor(context))
}
