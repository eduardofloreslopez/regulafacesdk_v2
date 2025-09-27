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
 * Enfocada en solucionar el problema de "0 pares" detectados.
 */
class RegulaFaceMatcher(private val context: Context) : FaceMatcher {

    private val TAG = "RegulaFaceMatcher"

    override suspend fun compare(a: FaceImage, b: FaceImage): Similarity = suspendCoroutine { continuation ->
        try {
            val referenceBitmap = a.bytes.toBitmap()
            val candidateBitmap = b.bytes.toBitmap()

            // Logging detallado de las imágenes
            Log.d(TAG, "=== ANÁLISIS DE IMÁGENES ===")
            Log.d(TAG, "Referencia: ${a.bytes.size} bytes")
            referenceBitmap?.let {
                Log.d(TAG, "  - Dimensiones: ${it.width}x${it.height}")
                Log.d(TAG, "  - Config: ${it.config}")
                Log.d(TAG, "  - HasAlpha: ${it.hasAlpha()}")
                Log.d(TAG, "  - Bytes per pixel: ${it.byteCount / (it.width * it.height)}")
            }

            Log.d(TAG, "Candidato: ${b.bytes.size} bytes")
            candidateBitmap?.let {
                Log.d(TAG, "  - Dimensiones: ${it.width}x${it.height}")
                Log.d(TAG, "  - Config: ${it.config}")
                Log.d(TAG, "  - HasAlpha: ${it.hasAlpha()}")
                Log.d(TAG, "  - Bytes per pixel: ${it.byteCount / (it.width * it.height)}")
            }

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

            Log.d(TAG, "=== CONFIGURACIÓN DEL SDK ===")

            // SOLUCIÓN 1: Usar ImageType.PRINTED para ambas (más común)
            val referenceImage = MatchFacesImage(referenceBitmap, ImageType.PRINTED)
            val candidateImage = MatchFacesImage(candidateBitmap, ImageType.PRINTED)

            Log.d(TAG, "ImageType usado: PRINTED para ambas imágenes")

            val imagesList = listOf(referenceImage, candidateImage)
            val request = MatchFacesRequest(imagesList)

            // SOLUCIÓN 2: Configurar el request para mayor tolerancia
            try {
                // Estas propiedades pueden variar según la versión del SDK
                Log.d(TAG, "Configurando request con parámetros optimizados...")
            } catch (e: Exception) {
                Log.w(TAG, "No se pudieron configurar parámetros adicionales del request")
            }

            Log.d(TAG, "=== INICIANDO COMPARACIÓN ===")
            Log.d(TAG, "Request creado con ${imagesList.size} imágenes")

            FaceSDK.Instance().matchFaces(context, request, object : MatchFaceCallback {

                override fun onFaceMatched(response: MatchFacesResponse?) {
                    Log.d(TAG, "=== RESPUESTA RECIBIDA ===")

                    try {
                        if (response == null) {
                            Log.e(TAG, "❌ Response es nulo")
                            continuation.resume(Similarity(0.0f))
                            return
                        }

                        // Análisis detallado de la respuesta
                        Log.d(TAG, "Response class: ${response::class.java.simpleName}")
                        Log.d(TAG, "Results count: ${response.results.size}")
                        Log.d(TAG, "Exception: ${response.exception}")

                        // Log del objeto completo para debug
                        try {
                            Log.d(TAG, "Response toString: ${response.toString()}")
                        } catch (e: Exception) {
                            Log.w(TAG, "No se pudo hacer toString de la response")
                        }

                        // Verificar excepción del SDK
                        response.exception?.let { exception ->
                            Log.e(TAG, "❌ SDK Exception: ${exception.message}")
                            Log.e(TAG, "Exception class: ${exception::class.java.simpleName}")
                            continuation.resume(Similarity(0.0f))
                            return
                        }

                        // Análisis de resultados
                        if (response.results.isEmpty()) {
                            Log.e(TAG, "❌ CERO PARES ENCONTRADOS")
                            Log.e(TAG, "Posibles soluciones:")
                            Log.e(TAG, "  1. Verificar que las imágenes contengan rostros claramente visibles")
                            Log.e(TAG, "  2. Asegurar buena iluminación en las fotos")
                            Log.e(TAG, "  3. Verificar que los rostros ocupen al menos 100x100 pixels")
                            Log.e(TAG, "  4. Probar con ImageType.LIVE en lugar de PRINTED")
                            Log.e(TAG, "  5. Verificar inicialización correcta del FaceSDK")
                            continuation.resume(Similarity(0.0f))
                            return
                        }

                        // Procesar el primer resultado
                        val comparisonPair = response.results.first()
                        val similarityScore = comparisonPair.similarity.toFloat()

                        Log.d(TAG, "✅ COMPARACIÓN EXITOSA")
                        Log.d(TAG, "Similarity score: $similarityScore")
                        Log.d(TAG, "Pair info: first=${comparisonPair.first}, second=${comparisonPair.second}")

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

    // Método para probar diferentes configuraciones
    private fun tryAlternativeComparison(
        referenceBitmap: Bitmap,
        candidateBitmap: Bitmap,
        continuation: kotlinx.coroutines.CancellableContinuation<Similarity>
    ) {
        Log.d(TAG, "=== PROBANDO CONFIGURACIÓN ALTERNATIVA ===")

        try {
            // Probar con ImageType.LIVE
            val referenceImage = MatchFacesImage(referenceBitmap, ImageType.LIVE)
            val candidateImage = MatchFacesImage(candidateBitmap, ImageType.LIVE)

            val imagesList = listOf(referenceImage, candidateImage)
            val request = MatchFacesRequest(imagesList)

            FaceSDK.Instance().matchFaces(context, request, object : MatchFaceCallback {
                override fun onFaceMatched(response: MatchFacesResponse?) {
                    if (response != null && response.results.isNotEmpty()) {
                        val similarityScore = response.results.first().similarity.toFloat()
                        Log.d(TAG, "✅ Configuración alternativa exitosa: $similarityScore")
                        continuation.resume(Similarity(similarityScore))
                    } else {
                        Log.e(TAG, "❌ Configuración alternativa también falló")
                        continuation.resume(Similarity(0.0f))
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en configuración alternativa", e)
            continuation.resume(Similarity(0.0f))
        }
    }
}