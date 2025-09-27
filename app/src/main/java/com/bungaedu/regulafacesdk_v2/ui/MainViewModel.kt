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

/**
 * ViewModel principal que orquesta el flujo:
 * captura/selección de imágenes, comparación facial y estado de UI.
 *
 * Responsabilidades:
 * - Observar la disponibilidad del SDK (FaceSdkManager.isReady) y reflejarlo en la UI.
 * - Lanzar la captura facial vía [FaceCaptureLauncher].
 * - Seleccionar imágenes desde galería vía [MediaPicker].
 * - Comparar rostros usando [FaceMatcher].
 * - Exponer y mutar el estado inmutable de pantalla [MainUiState].
 * - Verificar conectividad previa a acciones sensibles mediante [ConnectivityChecker].
 *
 * Dependencias inyectadas (arquitectura orientada a interfaces):
 * @param captureLauncher Gateway para lanzar la UI de captura del SDK.
 * @param matcher Motor de comparación facial (SDK real o mock).
 * @param mediaPicker Abstracción para selección de imagen (galería).
 * @param faceSdkManager Gestor del ciclo de vida del SDK (init/deinit + estado).
 * @param connectivity Comprobación de conectividad de red.
 */
class MainViewModel(
    private val captureLauncher: FaceCaptureLauncher,
    private val matcher: FaceMatcher,
    private val mediaPicker: MediaPicker,
    private val faceSdkManager: FaceSdkManager,
    private val connectivity: ConnectivityChecker
) : ViewModel() {

    /** Estado único y observable de la pantalla. */
    private val _ui = MutableStateFlow(MainUiState())
    val ui: StateFlow<MainUiState> = _ui

    /**
     * Suscribe el estado de disponibilidad del SDK y lo refleja en la UI.
     * Nota: si se prefiere evitar trabajo en `init`, mover la suscripción a
     * un méto.do explícito (p. ej. `onAppear()` desde la UI).
     */
    init {
        viewModelScope.launch {
            faceSdkManager.isReady.collect { ready ->
                _ui.update { it.copy(isSdkReady = ready) }
            }
        }
    }

    /** Cambia el modo de captura (pasivo/activo). */
    fun setCaptureMode(mode: CaptureMode) {
        _ui.value = _ui.value.copy(captureMode = mode)
    }

    /**
     * Solicita captura facial a través del SDK.
     *
     * @param activity Activity necesaria por el contrato del SDK (permisos/UI).
     * Resultado:
     * - Éxito: rellena `faceA` y, si ya existe, `faceB` (en orden).
     * - Error/Cancel: muestra mensaje en `errorMessage`.
     */
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

    /**
     * Lanza el selector de imagen (galería) y actualiza `faceA/faceB`.
     * Si el usuario cancela, simplemente desmarca `isBusy`.
     */
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

    /**
     * Ejecuta la comparación facial entre `faceA` y `faceB`.
     * - Valida que haya dos imágenes.
     * - Muestra errores amigables en `errorMessage`.
     * - Publica el resultado en `similarity`.
     */
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

    /** Restablece el flujo: limpia imágenes, resultado y errores. */
    fun resetFlow() {
        _ui.value =
            _ui.value.copy(faceA = null, faceB = null, similarity = null, errorMessage = null)
    }

    /** Verificación sincrónica de conectividad (para prechecks rápidos). */
    fun checkInternet(): Boolean = connectivity.isOnlineNow()
}
