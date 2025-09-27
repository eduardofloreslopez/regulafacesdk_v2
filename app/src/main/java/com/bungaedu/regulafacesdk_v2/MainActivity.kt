package com.bungaedu.regulafacesdk_v2

import android.os.Bundle
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

/**
 * Actividad principal de la aplicación.
 *
 * Se encarga de:
 * - Configurar el entorno de Compose como contenido principal.
 * - Integrar el flujo de selección de imágenes desde galería mediante
 *   [ActivityResultContracts.StartActivityForResult].
 * - Inyectar [MainViewModel] con Koin, pasando dinámicamente la implementación
 *   concreta de [MediaPicker].
 * - Definir las callbacks de UI que conectan la capa visual ([MainScreen]) con
 *   la lógica del ViewModel.
 *
 * Flujo general:
 * - El usuario puede elegir modo de captura (pasivo/activo).
 * - Capturar una foto con el SDK.
 * - Seleccionar una imagen desde galería.
 * - Comparar ambas imágenes (previa verificación de internet).
 * - Reiniciar el flujo cuando lo desee.
 */
class MainActivity : ComponentActivity() {
    private lateinit var androidMediaPicker: AndroidMediaPicker

    /**
     * Launcher clásico de Activity Result para la selección de imágenes.
     * Se integra fuera de Compose para manejar el resultado en [AndroidMediaPicker].
     */
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (::androidMediaPicker.isInitialized) {
                lifecycleScope.launch {
                    androidMediaPicker.onActivityResult(result.resultCode, result.data)
                }
            }
        }

    /**
     * Inyección del [MainViewModel] con Koin, recibiendo como parámetro
     * una implementación concreta de [MediaPicker].
     */
    private val mainViewModel: MainViewModel by viewModel {
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
