package org.example.project.scanner

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.Size
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

    @SuppressLint("RestrictedApi")
    @OptIn(ExperimentalGetImage::class)
    override fun startScanning(onResult: (String) -> Unit) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Configuración de Preview con tamaño predeterminado
            val preview = Preview.Builder()
                .setDefaultResolution(Size(1280, 720)) // Configuración fija de resolución
                .build()

            // Configuración de ImageAnalysis con tamaño predeterminado
            val imageAnalysis = ImageAnalysis.Builder()
                .setDefaultResolution(Size(640, 480)) // Resolución adecuada para análisis
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // Configuración del análisis de imágenes
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
                } else {
                    imageProxy.close()
                }
            }

            // Vincular los casos de uso
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(context))
    }

    override fun stopScanning() {
        cameraProviderFuture.get().unbindAll()
    }
}
