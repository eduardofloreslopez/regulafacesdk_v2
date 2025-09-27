package com.bungaedu.regulafacesdk_v2.data.gateway.impl

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode
import com.regula.facesdk.FaceSDK
import com.regula.facesdk.callback.FaceCaptureCallback
import com.regula.facesdk.callback.LivenessCallback
import com.regula.facesdk.model.results.FaceCaptureResponse
import com.regula.facesdk.model.results.LivenessResponse
import java.io.ByteArrayOutputStream

/**
 * Implementación REAL de captura con Regula Face SDK.
 * - PASIVO: Selfie sin reto (FaceCaptureActivity)
 * - ACTIVO: Liveness con reto (UI del SDK)
 */
class RegulaFaceCaptureLauncher : FaceCaptureLauncher {

    override fun start(
        activity: Activity,
        mode: CaptureMode,
        onResult: (Result<FaceImage>) -> Unit
    ) {
        when (mode) {
            CaptureMode.PASSIVE -> startPassiveCapture(activity, onResult)
            CaptureMode.ACTIVE -> startActiveLiveness(activity, onResult)
        }
    }

    // --- PASIVO (Face Capture UI) ---
    private fun startPassiveCapture(
        activity: Activity,
        onResult: (Result<FaceImage>) -> Unit
    ) {
        FaceSDK.Instance().presentFaceCaptureActivity(
            activity,
            FaceCaptureCallback { response: FaceCaptureResponse ->
                val ex = response.exception
                if (ex != null) {
                    onResult(Result.failure(ex))
                    return@FaceCaptureCallback
                }
                val image = response.image
                if (image == null) {
                    onResult(Result.failure(IllegalStateException("FaceCaptureResponse.image == null")))
                    return@FaceCaptureCallback
                }
                // SDK ya nos da el byte[] del bitmap
                val bytes = image.imageData
                if (bytes == null || bytes.isEmpty()) {
                    onResult(Result.failure(IllegalStateException("Imagen vacía en captura pasiva")))
                    return@FaceCaptureCallback
                }
                onResult(Result.success(FaceImage(bytes)))
            }
        )
    }

    // --- ACTIVO (Liveness UI) ---
    private fun startActiveLiveness(
        activity: Activity,
        onResult: (Result<FaceImage>) -> Unit
    ) {
        FaceSDK.Instance().startLiveness(
            activity,
            LivenessCallback { response: LivenessResponse ->
                val ex = response.exception
                if (ex != null) {
                    onResult(Result.failure(ex))
                    return@LivenessCallback
                }
                val bmp = response.bitmap
                if (bmp == null) {
                    onResult(Result.failure(IllegalStateException("LivenessResponse.bitmap == null")))
                    return@LivenessCallback
                }
                val bytes = bmp.toJpeg()
                onResult(Result.success(FaceImage(bytes)))
            }
        )
    }

    // Utilidad: convertir Bitmap de liveness a JPEG
    private fun Bitmap.toJpeg(quality: Int = 92): ByteArray {
        val out = ByteArrayOutputStream()
        this.compress(CompressFormat.JPEG, quality, out)
        return out.toByteArray()
    }
}
