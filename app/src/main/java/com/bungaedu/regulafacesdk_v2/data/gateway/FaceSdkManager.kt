package com.bungaedu.regulafacesdk_v2.data.gateway

import kotlinx.coroutines.flow.StateFlow

/**
 * Abstracción responsable de gestionar el ciclo de vida del SDK de reconocimiento facial.
 */
interface FaceSdkManager {
    val isReady: StateFlow<Boolean>
    fun initialize()
    fun deinitialize()
}