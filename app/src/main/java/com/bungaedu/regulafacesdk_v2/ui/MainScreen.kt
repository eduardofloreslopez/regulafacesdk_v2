package com.bungaedu.regulafacesdk_v2.ui

import android.Manifest
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode
import com.bungaedu.regulafacesdk_v2.ui.model.MainUiState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.font.FontWeight

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

    Scaffold(topBar = { MainTopBar() }) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                val chipColor =
                    if (state.isSdkReady) Color.Green else MaterialTheme.colorScheme.error
                AssistChip(
                    onClick = { },
                    label = { Text(if (state.isSdkReady) "SDK: Listo" else "SDK: No listo") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = chipColor)
                )
                Spacer(Modifier.width(12.dp))
                SegmentedButtons(state.captureMode, onSelectMode)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primer FacePreview y su botón, dentro de un Column
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally // Opcional: Centra los elementos horizontalmente
                ) {
                    FacePreview("Imagen A (captura)", state.faceA, Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp)) // Espacio entre FacePreview y Button
                    Button(
                        onClick = {
                            cameraPermLauncher.launch(Manifest.permission.CAMERA)
                            onCaptureClick
                        },
                        enabled = state.isSdkReady && !state.isBusy && state.faceA == null,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Capturar") }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally // Opcional: Centra los elementos horizontalmente
                ) {
                    FacePreview("Imagen B (galería)", state.faceB, Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp)) // Espacio entre FacePreview y Button
                    Button(
                        onClick = onPickFromGalleryClick,
                        enabled = !state.isBusy && state.faceA != null && state.faceB == null,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Elegir") }
                }
            }

            state.similarity?.let { sim ->
                Card {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Similitud: ${sim.percent}%",
                            style = MaterialTheme.typography.headlineLarge
                        )
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
                Button(
                    onClick = onCompareClick,
                    enabled = state.faceA != null && state.faceB != null && !state.isBusy && state.isSdkReady && state.similarity == null,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Comparar") }

                OutlinedButton(
                    onClick = onResetClick,
                    enabled = (state.faceA != null || state.faceB != null || state.similarity != null) && !state.isBusy,
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
                // Coil se encarga de la decodificación, reescalado y caching de forma eficiente.
                AsyncImage(
                    // Configuramos la solicitud de imagen
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bytes) // Coil acepta el array de bytes (byte[]) directamente
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    // Modificadores para el diseño
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    // Asegura que la imagen se ajuste y llene el espacio
                    contentScale = ContentScale.Crop
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Column {
                Text(
                    text = "Regula Face Demo",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

