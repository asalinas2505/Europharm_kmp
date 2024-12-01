package org.example.project.ui

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

                preview.setSurfaceProvider { request ->
                    val surface = surfaceView.holder.surface
                    if (surface != null && surface.isValid) {
                        request.provideSurface(surface, ContextCompat.getMainExecutor(ctx)) {}
                    }
                }

                val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.bindToLifecycle(
                    scanner.lifecycleOwner,
                    cameraSelector,
                    preview
                )
            }, ContextCompat.getMainExecutor(ctx))

            surfaceView
        }
    )
}
