package com.bungaedu.regulafacesdk_v2.data.gateway.impl.regula

import android.app.Application
import android.util.Log
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import com.regula.facesdk.FaceSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementación de [FaceSdkManager] que encapsula la inicialización
 * y liberación de recursos del SDK de Regula Face.
 *
 * Esta clase se encarga de:
 * - Inicializar el SDK con el contexto de aplicación.
 * - Exponer el estado de disponibilidad del SDK mediante un [StateFlow].
 * - Liberar los recursos cuando ya no se necesiten.
 *
 * @property app [Application] utilizado para inicializar el SDK.
 */
class RegulaSdkManagerImpl(
    private val app: Application
) : FaceSdkManager {

    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    override fun initialize() {
        FaceSDK.Instance().initialize(app) { success, error ->
            _isReady.value = success
            Log.i("FaceSDK", "initialize -> success=$success, error=${error?.message}")
        }
    }

    override fun deinitialize() {
        FaceSDK.Instance().deinitialize()
        _isReady.value = false
        Log.i("FaceSDK", "deinitialize -> ready=false")
    }
}
