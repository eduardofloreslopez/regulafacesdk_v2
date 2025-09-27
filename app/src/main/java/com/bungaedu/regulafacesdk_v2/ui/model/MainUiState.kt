package com.bungaedu.regulafacesdk_v2.ui.model

import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.data.model.Similarity

/**
 * Estado de la interfaz principal de la aplicación.
 *
 * Esta clase actúa como un contenedor inmutable (state holder) que describe
 * todos los datos que la UI necesita para renderizarse en un momento dado.
 *
 * Se sigue el patrón de "single source of truth" en MVVM:
 * - El [MainViewModel] expone una instancia de [MainUiState].
 * - La UI observa este estado y se redibuja automáticamente ante cambios.
 *
 * @property isSdkReady Indica si el SDK de reconocimiento facial está inicializado y listo.
 * @property isBusy Marca si hay un proceso en ejecución (captura, comparación, etc.).
 * @property captureMode Modo de captura actual (pasivo o activo).
 * @property faceA Primera imagen facial.
 * @property faceB Segunda imagen facial.
 * @property similarity Resultado de la comparación entre [faceA] y [faceB], o `null` si aún no se ha realizado.
 * @property errorMessage Mensaje de error para mostrar en la UI en caso de fallo.
 */
data class MainUiState(
    val isSdkReady: Boolean = false,
    val isBusy: Boolean = false,
    val captureMode: CaptureMode = CaptureMode.PASSIVE,
    val faceA: FaceImage? = null,
    val faceB: FaceImage? = null,
    val similarity: Similarity? = null,
    val errorMessage: String? = null
)
