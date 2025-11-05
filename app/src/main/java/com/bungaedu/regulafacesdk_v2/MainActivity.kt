package com.bungaedu.regulafacesdk_v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bungaedu.regulafacesdk_v2.data.gateway.MediaPicker
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.AndroidMediaPicker
import com.bungaedu.regulafacesdk_v2.ui.screens.MainScreen
import com.bungaedu.regulafacesdk_v2.ui.RecognizeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class MainActivity : ComponentActivity() {
    private lateinit var androidMediaPicker: AndroidMediaPicker

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (::androidMediaPicker.isInitialized) {
                lifecycleScope.launch {
                    androidMediaPicker.onActivityResult(result.resultCode, result.data)
                }
            }
        }

    private val recognizeViewModel: RecognizeViewModel by viewModel {
        val picker: MediaPicker = getKoin().get {
            parametersOf(this@MainActivity, galleryLauncher)
        }
        androidMediaPicker = picker as AndroidMediaPicker
        parametersOf(picker)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MainScreen(recognizeViewModel = recognizeViewModel)
        }
    }
}
