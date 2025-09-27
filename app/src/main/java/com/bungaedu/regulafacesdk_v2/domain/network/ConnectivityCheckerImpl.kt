package com.bungaedu.regulafacesdk_v2.domain.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow

class ConnectivityCheckerImpl(private val context: Context) : ConnectivityChecker {
    private val _isOnline = MutableStateFlow(checkNow())

    override fun isOnlineNow(): Boolean = checkNow()

    private fun checkNow(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun refresh() {
        _isOnline.value = checkNow()
    }
}