package com.bungaedu.regulafacesdk_v2.data.gateway.impl

import android.app.Application
import android.util.Log
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import com.regula.facesdk.FaceSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FaceSdkManagerImpl(
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
