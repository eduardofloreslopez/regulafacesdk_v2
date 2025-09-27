package com.bungaedu.regulafacesdk_v2.data.gateway

import android.app.Activity
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode

/**
 * Abstracción para lanzar un proceso de captura facial mediante el SDK.
 */
interface FaceCaptureLauncher {
    fun start(
        activity: Activity,
        mode: CaptureMode,
        onResult: (Result<FaceImage>) -> Unit
    )
}
