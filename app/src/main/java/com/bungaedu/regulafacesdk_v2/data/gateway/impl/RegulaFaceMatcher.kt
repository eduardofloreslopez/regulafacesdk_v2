package com.bungaedu.regulafacesdk_v2.data.gateway.impl

import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.data.model.Similarity

/**
 * Implementa aquí la comparación REAL con Regula (normalmente módulo core-match).
 * Debe devolver un score 0..1.
 */
class RegulaFaceMatcher : FaceMatcher {
    override suspend fun compare(a: FaceImage, b: FaceImage): Similarity {
        // TODO: Construye la request del matcher de Regula con a.bytes y b.bytes
        // Llama al motor de match y devuelve el score normalizado 0..1.
        throw UnsupportedOperationException("Match Regula no implementado")
    }
}
