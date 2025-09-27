package com.bungaedu.regulafacesdk_v2.data.gateway.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.data.model.Similarity

// Importaciones del SDK de Regula
import com.regula.facesdk.FaceSDK
import com.regula.facesdk.callback.MatchFaceCallback
import com.regula.facesdk.enums.ImageType
import com.regula.facesdk.model.MatchFacesImage
import com.regula.facesdk.model.results.matchfaces.MatchFacesComparedFacesPair
import com.regula.facesdk.model.results.matchfaces.MatchFacesResponse
import com.regula.facesdk.request.MatchFacesRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private fun ByteArray.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeByteArray(this, 0, this.size)
    } catch (e: Exception) {
        null
    }
}

/**
 * Implementación del Gateway de comparación usando la API MatchFaces del Regula FaceSDK.
 * Requiere el Context en el constructor para las llamadas al SDK.
 */
class RegulaFaceMatcher(private val context: Context) : FaceMatcher {

    private val TAG = "RegulaFaceMatcher"

    override suspend fun compare(a: FaceImage, b: FaceImage): Similarity = suspendCoroutine { continuation ->
        try {
            val referenceBitmap = a.bytes.toBitmap()
            Log.d(TAG, "Referencia: ${a.bytes.size} bytes -> bitmap=${referenceBitmap != null}")

            val candidateBitmap = b.bytes.toBitmap()
            Log.d(TAG, "Candidato: ${b.bytes.size} bytes -> bitmap=${candidateBitmap != null}")

            // Validación de bitmaps nulos
            if (referenceBitmap == null) {
                Log.e(TAG, "No se pudo decodificar la imagen de referencia")
                continuation.resume(Similarity(0.0f))
                return@suspendCoroutine
            }

            if (candidateBitmap == null) {
                Log.e(TAG, "No se pudo decodificar la imagen candidata")
                continuation.resume(Similarity(0.0f))
                return@suspendCoroutine
            }

            val referenceImage = MatchFacesImage(referenceBitmap, ImageType.LIVE)
            val candidateImage = MatchFacesImage(candidateBitmap, ImageType.PRINTED)
            Log.d(TAG, "Creando request con ${listOf(referenceImage, candidateImage).size} imágenes")

            val allImagesForComparison = listOf(referenceImage, candidateImage)
            val request = MatchFacesRequest(allImagesForComparison)

            // Llamada al SDK con context como primer parámetro
            FaceSDK.Instance().matchFaces(context, request, object : MatchFaceCallback {

                override fun onFaceMatched(response: MatchFacesResponse?) {
                    Log.d(TAG, "Respuesta recibida: ${response?.results?.size ?: 0} pares")
                    Log.d(TAG, "Raw response: ${response?.toString()}")
                    try {
                        if (response == null) {
                            Log.e(TAG, "Response es nulo")
                            continuation.resume(Similarity(0.0f))
                            return
                        }

                        // CORRECCIÓN: Acceso correcto a la estructura de resultados
                        val comparisonPair: MatchFacesComparedFacesPair? =
                            response.results.firstOrNull()

                        if (comparisonPair != null) {
                            // CORRECCIÓN: Conversión explícita de Double a Float
                            val similarityScore = comparisonPair.similarity.toFloat()
                            Log.d(TAG, "Comparación exitosa. Similarity: $similarityScore")
                            continuation.resume(Similarity(similarityScore))
                        } else {
                            Log.e(TAG, "Comparación fallida: No se encontraron pares de rostros.")
                            continuation.resume(Similarity(0.0f))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error procesando resultado de comparación", e)
                        continuation.resumeWithException(e)
                    }
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "Error general en compare()", e)
            continuation.resumeWithException(e)
        }
    }
}