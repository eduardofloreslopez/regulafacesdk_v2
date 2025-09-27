package com.bungaedu.regulafacesdk_v2.ui.model

import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.data.model.Similarity

data class MainUiState(
    val isSdkReady: Boolean = false,
    val isBusy: Boolean = false,
    val captureMode: CaptureMode = CaptureMode.PASSIVE,
    val faceA: FaceImage? = null,
    val faceB: FaceImage? = null,
    val similarity: Similarity? = null,
    val errorMessage: String? = null
)
