package org.example.project.scanner

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class AndroidScanner(
    val context: Context,
    val lifecycleOwner: LifecycleOwner
) : Scanner {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_DATA_MATRIX
            ).build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun startScanning(onResult: (String) -> Unit) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Configuración de Preview
            val preview = Preview.Builder().build()

            // Configuración de ImageAnalysis
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
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
                            Log.e("AndroidScanner", "Error: ${it.message}")
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            // Asegúrate de desvincular los casos previos antes de agregar nuevos
            cameraProvider.unbindAll()

            try {
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("AndroidScanner", "Error binding use cases: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun stopScanning() {
        cameraProviderFuture.get().unbindAll()
    }
}
