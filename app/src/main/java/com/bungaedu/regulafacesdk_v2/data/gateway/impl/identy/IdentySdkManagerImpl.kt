package com.bungaedu.regulafacesdk_v2.data.gateway.impl.identy

import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IdentySdkManagerImpl : FaceSdkManager {
    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady

    override fun initialize() {
        try {
            // IdentySDK.initialize(context, license)
            _isReady.value = true
        } catch (e: Exception) {
            _isReady.value = false
        }
    }

    override fun deinitialize() {
        // IdentySDK.deinit()
        _isReady.value = false
    }
}
