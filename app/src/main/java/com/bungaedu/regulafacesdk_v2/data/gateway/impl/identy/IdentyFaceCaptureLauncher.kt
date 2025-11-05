package com.bungaedu.regulafacesdk_v2.data.gateway.impl.identy

import android.app.Activity
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IdentyFaceCaptureLauncher : FaceCaptureLauncher {

    override fun start(activity: Activity, mode: CaptureMode, onResult: (Result<FaceImage>) -> Unit) {
        // 🔹 Aquí integras la UI o flujo del SDK de Identy
        // Ejemplo simulado (en producción usa el método oficial del SDK)
        try {
            // IdentySDK.startCapture(activity) { result ->
            //     val bytes = result.image.toByteArray()
            //     onResult(Result.success(FaceImage(bytes)))
            // }

            // Mock temporal para pruebas
            val dummyBytes = ByteArray(100) { 0 }
            onResult(Result.success(FaceImage(dummyBytes)))

        } catch (e: Exception) {
            onResult(Result.failure(e))
        }
    }
}
