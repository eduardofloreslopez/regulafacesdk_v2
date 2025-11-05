package com.bungaedu.regulafacesdk_v2.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bungaedu.regulafacesdk_v2.domain.KEY_SELECTED_SDK
import com.bungaedu.regulafacesdk_v2.domain.PREFS_NAME
import com.bungaedu.regulafacesdk_v2.ui.model.CaptureMode
import com.bungaedu.regulafacesdk_v2.ui.model.SelectedSDK

@Composable
fun SettingsScreen(
    currentMode: CaptureMode = CaptureMode.PASSIVE,
    onModeChange: (CaptureMode) -> Unit = {},
    selectedSDK: SelectedSDK = SelectedSDK.REGULA_SDK,
    onSdkChange: (SelectedSDK) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Selecciona el modo de captura",
                style = MaterialTheme.typography.bodyMedium
            )

            CaptureModeChips(
                mode = currentMode,
                onModeChange = onModeChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selecciona el SDK",
                style = MaterialTheme.typography.bodyMedium
            )

            SelectedSDKChips(
                selectedSDK = selectedSDK,
                onSdkChange = onSdkChange
            )

        }
    }
}

/**
 * Grupo de botones segmentados para seleccionar el [CaptureMode].
 *
 * Modo ACTIVO queda deshabilitado por ahora.
 */
@Composable
fun CaptureModeChips(mode: CaptureMode, onModeChange: (CaptureMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = mode == CaptureMode.PASSIVE,
            onClick = { onModeChange(CaptureMode.PASSIVE) },
            label = { Text("Pasivo") }
        )
        FilterChip(
            selected = mode == CaptureMode.ACTIVE,
            onClick = { onModeChange(CaptureMode.ACTIVE) },
            label = { Text("Activo") },
            enabled = false
        )
    }
}

@Composable
fun SelectedSDKChips(
    selectedSDK: SelectedSDK,
    onSdkChange: (SelectedSDK) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selectedSDK == SelectedSDK.REGULA_SDK,
            onClick = { onSdkChange(SelectedSDK.REGULA_SDK) },
            label = { Text("RegulaSDK") }
        )
        FilterChip(
            selected = selectedSDK == SelectedSDK.IDENTY_SDK,
            onClick = { onSdkChange(SelectedSDK.IDENTY_SDK) },
            label = { Text("IdentySDK") },
        )
    }
}

/** Guarda el SDK seleccionado en SharedPreferences */
private fun saveSelectedSdk(context: Context, sdk: SelectedSDK) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_SELECTED_SDK, sdk.name).apply()
}
