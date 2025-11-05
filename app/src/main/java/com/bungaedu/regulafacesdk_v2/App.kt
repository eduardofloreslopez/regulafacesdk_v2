package com.bungaedu.regulafacesdk_v2

import android.app.Application
import android.content.Context
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import com.bungaedu.regulafacesdk_v2.di.appModule
import com.bungaedu.regulafacesdk_v2.di.provideSdkModule
import com.bungaedu.regulafacesdk_v2.ui.model.SelectedSDK
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

class App : Application() {

    private val faceSdkManager: FaceSdkManager by inject(FaceSdkManager::class.java)

    override fun onCreate() {
        super.onCreate()

        val selectedSdk = loadSelectedSdk()

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    appModule,
                    provideSdkModule(selectedSdk)
                )
            )
        }

        faceSdkManager.initialize()
    }

    override fun onTerminate() {
        super.onTerminate()
        faceSdkManager.deinitialize()
    }

    private fun loadSelectedSdk(): SelectedSDK {
        val prefs = getSharedPreferences("sdk_prefs", MODE_PRIVATE)
        val stored = prefs.getString("selected_sdk", SelectedSDK.REGULA_SDK.name)
        return try {
            SelectedSDK.valueOf(stored!!)
        } catch (_: Exception) {
            SelectedSDK.REGULA_SDK
        }
    }
}

