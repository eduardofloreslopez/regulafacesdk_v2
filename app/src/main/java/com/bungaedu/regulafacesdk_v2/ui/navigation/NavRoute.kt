package com.bungaedu.regulafacesdk_v2.ui.navigation

sealed class NavRoute(val route: String) {
    object Recognize : NavRoute("recognize")
    object Settings : NavRoute("settings")
}