package com.bungaedu.regulafacesdk_v2.data.gateway.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.data.model.Similarity

import com.regula.facesdk.FaceSDK
import com.regula.facesdk.callback.MatchFaceCallback
import com.regula.facesdk.enums.ImageType
import com.regula.facesdk.model.MatchFacesImage
import com.regula.facesdk.model.results.matchfaces.MatchFacesResponse
import com.regula.facesdk.request.MatchFacesRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private fun ByteArray.toBitmap(): Bitmap? {
    return try {
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.RGB_565 // Más eficiente
            inSampleSize = 1 // Sin downsampling por ahora
        }
        BitmapFactory.decodeByteArray(this, 0, this.size, options)
    } catch (e: Exception) {
        Log.e("BitmapDecoding", "Error decodificando bitmap", e)
        null
    }
}

/**
 * Implementación práctica del Gateway de comparación usando la API MatchFaces del Regula FaceSDK.
 */
class RegulaFaceMatcher(private val context: Context) : FaceMatcher {

    private val TAG = "RegulaFaceMatcher"

    override suspend fun compare(a: FaceImage, b: FaceImage): Similarity = suspendCoroutine { continuation ->
        try {
            val referenceBitmap = a.bytes.toBitmap()
            val candidateBitmap = b.bytes.toBitmap()

            // Validación de bitmaps
            if (referenceBitmap == null) {
                Log.e(TAG, "❌ No se pudo decodificar la imagen de referencia")
                continuation.resume(Similarity(0.0f))
                return@suspendCoroutine
            }

            if (candidateBitmap == null) {
                Log.e(TAG, "❌ No se pudo decodificar la imagen candidata")
                continuation.resume(Similarity(0.0f))
                return@suspendCoroutine
            }

            // Validación de tamaño mínimo
            if (referenceBitmap.width < 100 || referenceBitmap.height < 100) {
                Log.w(TAG, "⚠️ Imagen de referencia muy pequeña: ${referenceBitmap.width}x${referenceBitmap.height}")
            }
            if (candidateBitmap.width < 100 || candidateBitmap.height < 100) {
                Log.w(TAG, "⚠️ Imagen candidata muy pequeña: ${candidateBitmap.width}x${candidateBitmap.height}")
            }

            val referenceImage = MatchFacesImage(referenceBitmap, ImageType.PRINTED)
            val candidateImage = MatchFacesImage(candidateBitmap, ImageType.PRINTED)

            val imagesList = listOf(referenceImage, candidateImage)
            val request = MatchFacesRequest(imagesList)

            FaceSDK.Instance().matchFaces(context, request, object : MatchFaceCallback {

                override fun onFaceMatched(response: MatchFacesResponse?) {
                    try {
                        if (response == null) {
                            Log.e(TAG, "❌ Response es nulo")
                            continuation.resume(Similarity(0.0f))
                            return
                        }

                        // Procesar el primer resultado
                        val comparisonPair = response.results.first()
                        val similarityScore = comparisonPair.similarity.toFloat()

                        continuation.resume(Similarity(similarityScore))

                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error procesando resultado", e)
                        continuation.resumeWithException(e)
                    }
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error general en compare()", e)
            continuation.resumeWithException(e)
        }
    }
}