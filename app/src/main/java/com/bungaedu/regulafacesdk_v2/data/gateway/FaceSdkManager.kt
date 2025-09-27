package com.bungaedu.regulafacesdk_v2.data.gateway

import kotlinx.coroutines.flow.StateFlow

interface FaceSdkManager {
    val isReady: StateFlow<Boolean>
    fun initialize()
    fun deinitialize()
}