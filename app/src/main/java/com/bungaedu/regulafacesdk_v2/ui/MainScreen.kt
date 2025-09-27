package com.bungaedu.regulafacesdk_v2.ui

import android.Manifest
import android.R.attr.enabled
import android.app.Activity
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bungaedu.regulafacesdk_v2.MainActivity
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode
import com.bungaedu.regulafacesdk_v2.ui.model.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainUiState,
    onSelectMode: (CaptureMode) -> Unit,
    onCaptureClick: (Activity) -> Unit,
    onPickFromGalleryClick: () -> Unit,
    onCompareClick: () -> Unit,
    onResetClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity  // ✅ Conversión directa
    val cameraPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                onCaptureClick(activity)
            }
        }
    )

    Scaffold(topBar = { TopAppBar(title = { Text("Regula Face Demo") }) }) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                val chipColor =
                    if (state.isSdkReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                AssistChip(
                    onClick = { },
                    label = { Text(if (state.isSdkReady) "SDK: Listo" else "SDK: No listo") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = chipColor)
                )
                Spacer(Modifier.width(12.dp))
                SegmentedButtons(state.captureMode, onSelectMode)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FacePreview("Imagen A (captura)", state.faceA, Modifier.weight(1f))
                FacePreview("Imagen B (galería)", state.faceB, Modifier.weight(1f))
            }

            state.similarity?.let { sim ->
                Card {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Similitud: ${sim.percent}%", style = MaterialTheme.typography.headlineLarge)
                        LinearProgressIndicator(
                            progress = sim.percent / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }

            if (state.isBusy) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.errorMessage?.let { err ->
                AssistChip(
                    onClick = { },
                    label = { Text("Error: $err") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Pide permiso de cámara y la Activity ejecuta onCaptureClick(activity)
                Button(
                    onClick = {
                        cameraPermLauncher.launch(Manifest.permission.CAMERA)
                        onCaptureClick
                    },
                    enabled = state.isSdkReady && !state.isBusy,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Capturar rostro (SDK)") }

                // Galería → delega en la Activity (MediaPicker)
                OutlinedButton(
                    onClick = onPickFromGalleryClick,
                    enabled = !state.isBusy,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Elegir de galería") }

                Button(
                    onClick = onCompareClick,
                    enabled = state.faceA != null && state.faceB != null && !state.isBusy && state.isSdkReady,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Comparar") }

                OutlinedButton(
                    onClick = onResetClick,
                    enabled = (state.faceA != null || state.faceB != null || state.similarity != null),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Reiniciar flujo") }
            }
        }
    }
}

@Composable
private fun FacePreview(title: String, image: FaceImage?, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            val bytes = image?.bytes
            if (bytes != null && bytes.isNotEmpty()) {
                val bmp = remember(bytes) { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("Imagen inválida") }
                }
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) { Text("Sin imagen") }
            }
        }
    }
}

@Composable
private fun SegmentedButtons(mode: CaptureMode, onModeChange: (CaptureMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = mode == CaptureMode.PASSIVE,
            onClick = { onModeChange(CaptureMode.PASSIVE) },
            label = { Text("Pasivo") })
        FilterChip(
            selected = mode == CaptureMode.ACTIVE,
            onClick = { onModeChange(CaptureMode.ACTIVE) },
            label = { Text("Activo") },
            enabled = false
        )
    }
}
