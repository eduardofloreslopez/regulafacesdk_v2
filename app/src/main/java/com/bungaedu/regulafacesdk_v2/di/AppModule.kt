package com.bungaedu.regulafacesdk_v2.di

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import com.bungaedu.regulafacesdk_v2.data.gateway.MediaPicker
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.AndroidMediaPicker
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.FaceSdkManagerImpl
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.RegulaFaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.RegulaFaceMatcher
import com.bungaedu.regulafacesdk_v2.domain.network.ConnectivityCheckerImpl
import com.bungaedu.regulafacesdk_v2.domain.network.ConnectivityChecker
import com.bungaedu.regulafacesdk_v2.ui.RecognizeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ConnectivityChecker> { ConnectivityCheckerImpl(get()) } // requiere Context

    single<FaceCaptureLauncher> { RegulaFaceCaptureLauncher() }
    single<FaceMatcher> { RegulaFaceMatcher(androidContext()) }

    factory<MediaPicker> { (activity: Activity, launcher: ActivityResultLauncher<Intent>) ->
        AndroidMediaPicker(activity, launcher)
    }

    single<FaceSdkManager> { FaceSdkManagerImpl(androidContext().applicationContext as Application) }

    viewModel { (mediaPicker: MediaPicker) ->
        RecognizeViewModel(
            captureLauncher = get(),
            matcher = get(),
            mediaPicker = mediaPicker,
            faceSdkManager = get(),
            connectivity = get()
        )
    }
}
