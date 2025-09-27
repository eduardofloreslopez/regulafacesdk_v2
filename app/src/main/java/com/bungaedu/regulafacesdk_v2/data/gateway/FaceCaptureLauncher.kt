package com.bungaedu.regulafacesdk_v2.data.gateway

import android.app.Activity
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode

/**
 * Encapsula el lanzamiento de la UI de Regula y devuelve la imagen capturada.
 * Implementa aquí las llamadas reales del SDK (actividad/fragment/configuración pasivo/activo).
 */
interface FaceCaptureLauncher {
    fun start(
        activity: Activity,
        mode: CaptureMode,
        onResult: (Result<FaceImage>) -> Unit
    )
}
