package com.bungaedu.regulafacesdk_v2

import android.app.Application
import android.util.Log
import com.bungaedu.regulafacesdk_v2.di.appModule

import com.regula.facesdk.FaceSDK
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                appModule
            )
        }
        FaceSDK.Instance().initialize(this) { success, error ->
            Log.i("FaceSDK", "init=$success, error=${error?.message}")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        FaceSDK.Instance().deinitialize()
        Log.i("FaceSDK", "deinitialized")
    }
}
