package com.bungaedu.regulafacesdk_v2

import android.app.Application
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import com.bungaedu.regulafacesdk_v2.di.appModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private val faceSdkManager: FaceSdkManager by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        faceSdkManager.initialize()
    }

    override fun onTerminate() {
        super.onTerminate()

        faceSdkManager.deinitialize()
    }
}
