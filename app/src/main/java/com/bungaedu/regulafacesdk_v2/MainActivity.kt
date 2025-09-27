package com.bungaedu.regulafacesdk_v2

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.bungaedu.regulafacesdk_v2.data.gateway.MediaPicker
import com.bungaedu.regulafacesdk_v2.data.gateway.impl.AndroidMediaPicker
import com.bungaedu.regulafacesdk_v2.ui.MainViewModel
import com.bungaedu.regulafacesdk_v2.ui.MainScreen
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Mantendremos la referencia concreta para reenviar el resultado del launcher
    private lateinit var androidMediaPicker: AndroidMediaPicker

    // Registrar el launcher CLÁSICO de Activity Result (fuera de Compose)
    // 1) En el launcher, envolver la llamada suspend
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (::androidMediaPicker.isInitialized) {
                lifecycleScope.launch {
                    androidMediaPicker.onActivityResult(result.resultCode, result.data)
                }
            }
        }

    // Inyectamos el ViewModel pasando el MediaPicker como parámetro
    // (lo creamos en onCreate, donde además capturamos la implementación concreta)
    private val mainViewModel: MainViewModel by viewModel {
        val picker: MediaPicker = getKoin().get {
            parametersOf(this@MainActivity, galleryLauncher as androidx.activity.result.ActivityResultLauncher<Intent>)
        }
        // Guardamos la implementación concreta para recibir el callback del launcher
        androidMediaPicker = picker as AndroidMediaPicker

        // Le pasamos el MediaPicker al VM
        parametersOf(picker)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val state = mainViewModel.ui.collectAsStateWithLifecycle().value
            MainScreen(
                state = state,
                onSelectMode = { mode: CaptureMode ->
                    mainViewModel.setCaptureMode(mode)
                },
                onCaptureClick = {
                    // El contrato actual de captura requiere Activity
                    mainViewModel.requestCapture(this)
                },
                onPickFromGalleryClick = {
                    mainViewModel.requestGalleryImage()
                },
                onCompareClick = {
                    if (mainViewModel.checkInternet()) {
                        mainViewModel.compareFaces()
                    } else {
                        Toast.makeText(
                            this,
                            "Necesitas internet para poder realizar la comprobación",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onResetClick = {
                    mainViewModel.resetFlow()
                }
            )
        }
    }
}
