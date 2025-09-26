package com.bungaedu.regulafacesdk_v2

import android.app.Application
import android.util.Log

import com.regula.facesdk.FaceSDK

class App : Application() {
    override fun onCreate() {
        super.onCreate()
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
