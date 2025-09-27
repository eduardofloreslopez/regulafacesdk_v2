package com.bungaedu.regulafacesdk_v2.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import com.bungaedu.regulafacesdk_v2.data.gateway.MediaPicker
import com.bungaedu.regulafacesdk_v2.domain.network.ConnectivityChecker
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode
import com.bungaedu.regulafacesdk_v2.ui.model.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val captureLauncher: FaceCaptureLauncher,
    private val matcher: FaceMatcher,
    private val mediaPicker: MediaPicker,
    private val faceSdkManager: FaceSdkManager,
    private val connectivity: ConnectivityChecker
) : ViewModel() {

    private val _ui = MutableStateFlow(MainUiState())
    val ui: StateFlow<MainUiState> = _ui

    init {
        // Observa el readiness del SDK y propágalo al estado de UI
        viewModelScope.launch {
            faceSdkManager.isReady.collect { ready ->
                _ui.update { it.copy(isSdkReady = ready) }
            }
        }
    }

    fun setCaptureMode(mode: CaptureMode) {
        _ui.value = _ui.value.copy(captureMode = mode)
    }

    /** Lanza la UI de Regula (requiere Activity por contrato actual). */
    fun requestCapture(activity: Activity) {
        val snapshot = _ui.value
        _ui.value = snapshot.copy(isBusy = true, errorMessage = null)

        captureLauncher.start(activity, snapshot.captureMode) { result ->
            result
                .onSuccess { image ->
                    val cur = _ui.value
                    val a = cur.faceA ?: image
                    val b = cur.faceA?.let { cur.faceB ?: image }
                    _ui.value = cur.copy(isBusy = false, faceA = a, faceB = b ?: cur.faceB)
                }
                .onFailure {
                    _ui.value = _ui.value.copy(
                        isBusy = false,
                        errorMessage = it.message ?: "Captura cancelada o fallida"
                    )
                }
        }
    }

    /** Abre galería a través del MediaPicker inyectado. */
    fun requestGalleryImage() {
        _ui.value = _ui.value.copy(isBusy = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { mediaPicker.pickImage() }
                .onSuccess { face ->
                    val cur = _ui.value
                    if (face != null) {
                        val a = cur.faceA ?: face
                        val b = cur.faceA?.let { cur.faceB ?: face }
                        _ui.value = cur.copy(isBusy = false, faceA = a, faceB = b ?: cur.faceB)
                    } else {
                        _ui.value = cur.copy(isBusy = false)
                    }
                }
                .onFailure {
                    _ui.value = _ui.value.copy(
                        isBusy = false,
                        errorMessage = it.message ?: "Error al seleccionar imagen"
                    )
                }
        }
    }

    fun compareFaces() {
        val a = _ui.value.faceA
        val b = _ui.value.faceB
        if (a == null || b == null) {
            _ui.value = _ui.value.copy(errorMessage = "Selecciona dos imágenes")
            return
        }
        _ui.value = _ui.value.copy(isBusy = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { matcher.compare(a, b) }
                .onSuccess { sim ->
                    _ui.value = _ui.value.copy(isBusy = false, similarity = sim)
                }
                .onFailure {
                    _ui.value = _ui.value.copy(
                        isBusy = false,
                        errorMessage = it.message ?: "Error al comparar"
                    )
                }
        }
    }

    fun resetFlow() {
        _ui.value = _ui.value.copy(faceA = null, faceB = null, similarity = null, errorMessage = null)
    }

    /** Check rápido y síncrono usando el contrato de dominio */
    fun checkInternet(): Boolean = connectivity.isOnlineNow()
}
