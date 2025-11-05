package com.bungaedu.regulafacesdk_v2.di

import android.app.Application
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.FaceSdkManagerImpl
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.RegulaFaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.RegulaFaceMatcher
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val regulaSdkModule = module {
    single<FaceCaptureLauncher> { RegulaFaceCaptureLauncher() }
    single<FaceMatcher> { RegulaFaceMatcher(androidContext()) }
    single<FaceSdkManager> { FaceSdkManagerImpl(androidContext().applicationContext as Application) }
}
