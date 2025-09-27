package com.bungaedu.regulafacesdk_v2.domain.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Implementación de [ConnectivityChecker] que utiliza las APIs nativas de Android
 * para comprobar el estado de conectividad a Internet.
 *
 * Se basa en [ConnectivityManager] y [NetworkCapabilities] para determinar si
 * la red activa dispone de acceso a Internet.
 *
 * @property context Contexto de la aplicación necesario para acceder a los
 * servicios de conectividad de Android.
 */
class ConnectivityCheckerImpl(private val context: Context) : ConnectivityChecker {
    override fun isOnlineNow(): Boolean = checkNow()

    private fun checkNow(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}