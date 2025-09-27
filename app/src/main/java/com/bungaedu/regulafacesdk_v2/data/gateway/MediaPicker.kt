package com.bungaedu.regulafacesdk_v2.data.gateway

import com.bungaedu.regulafacesdk_v2.data.model.FaceImage

/**
 * Contrato para obtener una imagen desde la galería del dispositivo.
 * Oculta los detalles de Android (Intents, Photo Picker).
 */
interface MediaPicker {
    /**
     * Lanza el flujo de selección de imagen.
     * Devuelve un [FaceImage] si el usuario selecciona algo, o null si cancela.
     */
    suspend fun pickImage(): FaceImage?
}
