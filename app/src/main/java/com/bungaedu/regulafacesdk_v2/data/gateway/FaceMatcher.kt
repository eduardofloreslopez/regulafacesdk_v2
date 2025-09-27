package com.bungaedu.regulafacesdk_v2.data.gateway

import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.data.model.Similarity

/**
 * Encapsula la comparación de dos rostros usando el motor de Regula.
 * Debe devolver un score 0..1 (similitud).
 */
interface FaceMatcher {
    suspend fun compare(a: FaceImage, b: FaceImage): Similarity
}

