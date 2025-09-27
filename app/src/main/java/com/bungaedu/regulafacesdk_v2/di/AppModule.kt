package com.bungaedu.regulafacesdk_v2.di

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.gateway.MediaPicker
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.AndroidMediaPicker
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.RegulaFaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.RegulaFaceMatcher
import com.bungaedu.regulafacesdk_v2.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Regula (REALO)
    single<FaceCaptureLauncher> { RegulaFaceCaptureLauncher() }
    single<FaceMatcher> { RegulaFaceMatcher() }

    // MediaPicker Android con parámetros (Activity + Launcher)
    factory<MediaPicker> { (activity: Activity, launcher: ActivityResultLauncher<Intent>) ->
        AndroidMediaPicker(activity, launcher)
    }

    // ViewModel no conoce implementaciones
    viewModel { (mediaPicker: MediaPicker) ->
        MainViewModel(
            captureLauncher = get(),
            matcher = get(),
            mediaPicker = mediaPicker
        )
    }
}
