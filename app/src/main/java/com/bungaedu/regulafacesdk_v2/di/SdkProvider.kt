package com.bungaedu.regulafacesdk_v2.di

import com.bungaedu.regulafacesdk_v2.ui.model.SelectedSDK
import org.koin.core.module.Module

fun provideSdkModule(selected: SelectedSDK): Module {
    return when (selected) {
        SelectedSDK.REGULA_SDK -> regulaSdkModule
        SelectedSDK.IDENTY_SDK -> identySdkModule
    }
}
