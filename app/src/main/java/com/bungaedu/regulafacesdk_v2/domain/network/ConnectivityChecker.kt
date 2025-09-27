package com.bungaedu.regulafacesdk_v2.domain.network

interface ConnectivityChecker {
    fun isOnlineNow(): Boolean
}