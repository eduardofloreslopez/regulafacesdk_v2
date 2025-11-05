package com.bungaedu.regulafacesdk_v2.ui.screens

import MainTopBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bungaedu.regulafacesdk_v2.ui.RecognizeViewModel
import com.bungaedu.regulafacesdk_v2.ui.navigation.NavGraph
import com.bungaedu.regulafacesdk_v2.ui.navigation.NavRoute

@Composable
fun MainScreen(recognizeViewModel: RecognizeViewModel) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        topBar = { MainTopBar() },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Face, contentDescription = "Reconocer") },
                    label = { Text("Recognize") },
                    selected = currentRoute == NavRoute.Recognize.route,
                    onClick = {
                        navController.navigate(NavRoute.Recognize.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes") },
                    selected = currentRoute == NavRoute.Settings.route,
                    onClick = {
                        navController.navigate(NavRoute.Settings.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                recognizeViewModel = recognizeViewModel
            )
        }
    }
}
