package com.bungaedu.regulafacesdk_v2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bungaedu.regulafacesdk_v2.ui.RecognizeViewModel
import com.bungaedu.regulafacesdk_v2.ui.screens.SettingsScreen
import com.bungaedu.regulafacesdk_v2.ui.screens.RecognizeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    recognizeViewModel: RecognizeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Recognize.route
    ) {
        composable(NavRoute.Recognize.route) {
            val state = recognizeViewModel.ui.value
            RecognizeScreen(
                state = state,
                onSelectMode = { mode -> recognizeViewModel.setCaptureMode(mode) },
                onCaptureClick = { activity -> recognizeViewModel.requestCapture(activity) },
                onPickFromGalleryClick = { recognizeViewModel.requestGalleryImage() },
                onCompareClick = {
                    if (recognizeViewModel.checkInternet()) {
                        recognizeViewModel.compareFaces()
                    }
                },
                onResetClick = { recognizeViewModel.resetFlow() }
            )
        }

        composable(NavRoute.Settings.route) {
            SettingsScreen()
        }
    }
}
