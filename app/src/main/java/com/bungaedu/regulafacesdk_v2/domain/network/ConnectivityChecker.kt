package com.bungaedu.regulafacesdk_v2.domain.network

/**
 * Abstracción para comprobar el estado actual de conectividad a Internet.
 */
interface ConnectivityChecker {
    fun isOnlineNow(): Boolean
}