package com.bungaedu.regulafacesdk_v2.data.gateway

import com.bungaedu.regulafacesdk_v2.data.model.FaceImage

/**
 * Abstracción para seleccionar una imagen facial desde la galería u otra fuente de medios.
 */
interface MediaPicker {
    suspend fun pickImage(): FaceImage?
}
