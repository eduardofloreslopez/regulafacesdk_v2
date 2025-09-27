package com.bungaedu.regulafacesdk_v2.data.gateway

import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.data.model.Similarity

/**
 * Abstracción para comparar dos imágenes faciales mediante un motor de matching.
 */
interface FaceMatcher {
    suspend fun compare(a: FaceImage, b: FaceImage): Similarity
}

