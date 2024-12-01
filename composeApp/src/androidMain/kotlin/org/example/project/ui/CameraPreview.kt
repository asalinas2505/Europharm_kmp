package org.example.project.ui

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.example.project.scanner.AndroidScanner

@Composable
fun CameraPreview(scanner: AndroidScanner) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val surfaceView = SurfaceView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()

                // Configura el SurfaceProvider para la vista previa
                preview.setSurfaceProvider { request ->
                    val surface = surfaceView.holder.surface
                    if (surface != null && surface.isValid) {
                        request.provideSurface(surface, ContextCompat.getMainExecutor(ctx)) {}
                    }
                }

                // Configuración de la cámara
                val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll() // Limpia configuraciones anteriores
                try {
                    cameraProvider.bindToLifecycle(
                        scanner.lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            // Configura callbacks para el SurfaceHolder
            surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    // Configuración adicional si es necesario
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                    // Maneja cambios en el tamaño del SurfaceView
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    // Libera recursos si es necesario
                }
            })

            surfaceView
        }
    )
}
