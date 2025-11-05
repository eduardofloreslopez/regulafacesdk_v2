package com.bungaedu.regulafacesdk_v2.di

import android.app.Application
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceCaptureLauncher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceMatcher
import com.bungaedu.regulafacesdk_v2.data.gateway.FaceSdkManager
//import com.bungaedu.regulafacesdk_v2.data.gateway.impl.identy.IdentyFaceCaptureLauncher
//import com.bungaedu.regulafacesdk_v2.data.gateway.impl.identy.IdentyFaceMatcher
//import com.bungaedu.regulafacesdk_v2.data.gateway.impl.identy.IdentyFaceSdkManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val identySdkModule = module {
//    single<FaceCaptureLauncher> { IdentyFaceCaptureLauncher() }
//    single<FaceMatcher> { IdentyFaceMatcher(androidContext()) }
//    single<FaceSdkManager> { IdentyFaceSdkManagerImpl(androidContext().applicationContext as Application) }
}
