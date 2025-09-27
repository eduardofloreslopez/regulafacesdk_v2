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

/**
 * Pantalla principal que orquesta la interacción de usuario:
 * - Selección de modo de captura (pasivo/activo).
 * - Captura de imagen facial con el SDK.
 * - Selección de imagen desde galería.
 * - Comparación de similitud y visualización del resultado.
 *
 * Renderiza en base a [MainUiState] y delega acciones al ViewModel mediante callbacks.
 *
 * @param state Estado inmutable de la UI.
 * @param onSelectMode Cambia el modo de captura (pasivo/activo).
 * @param onCaptureClick Lanza el flujo de captura del SDK (requiere [Activity]).
 * @param onPickFromGalleryClick Abre la galería para seleccionar imagen.
 * @param onCompareClick Ejecuta la comparación de rostros.
 * @param onResetClick Reinicia el flujo (borra imágenes y resultado).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainUiState,
    onSelectMode: (CaptureMode) -> Unit,
    onCaptureClick: (Activity) -> Unit,
    onPickFromGalleryClick: () -> Unit,
    onCompareClick: () -> Unit,
    onResetClick: () -> Unit,
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FacePreview("Imagen A (captura)", state.faceA, Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FacePreview("Imagen B (galería)", state.faceB, Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
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
                            style = MaterialTheme.typography.headlineMedium
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

/**
 * Muestra una tarjeta con preview de una imagen facial (o marcador "Sin imagen").
 *
 * Utiliza Coil para renderizar con eficiencia posibles imágenes grandes.
 *
 * @param title Título de la tarjeta (ej. "Imagen A (captura)").
 * @param image Imagen facial a mostrar; si es null o vacía, se muestra marcador.
 * @param modifier Modificador Compose para tamaño/estilo.
 */
@Composable
private fun FacePreview(title: String, image: FaceImage?, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

            val bytes = image?.bytes

            if (bytes != null && bytes.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bytes)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
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

/**
 * Grupo de botones segmentados para seleccionar el [CaptureMode].
 *
 * Modo ACTIVO queda deshabilitado por ahora, dejando constancia de futura extensión.
 *
 * @param mode Modo actual seleccionado.
 * @param onModeChange Callback al pulsar un modo distinto.
 */
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

/**
 * Barra superior centrada para la demo.
 */
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

