package com.bungaedu.regulafacesdk_v2.data.gateway.impl

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bungaedu.regulafacesdk_v2.data.gateway.MediaPicker
import com.bungaedu.regulafacesdk_v2.data.model.FaceImage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación Android del MediaPicker usando StartActivityForResult().
 *
 * USO:
 *  - En la Activity registra un launcher de ActivityResultContracts.StartActivityForResult()
 *  - Inyecta activity + launcher vía Koin parametersOf(activity, launcher)
 *  - Reenvía el callback al método onActivityResult(...)
 */
class AndroidMediaPicker(
    private val activity: Activity,
    private val launcher: ActivityResultLauncher<Intent>
) : MediaPicker {

    private var pending: CompletableDeferred<FaceImage?>? = null

    suspend fun onActivityResult(resultCode: Int, data: Intent?) {
        val cont = pending ?: return
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            cont.complete(uri?.let { FaceImage(readBytes(it)) })
        } else {
            cont.complete(null)
        }
        pending = null
    }

    override suspend fun pickImage(): FaceImage? {
        val cont = CompletableDeferred<FaceImage?>()
        pending = cont

        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        launcher.launch(intent)

        return cont.await()
    }

    private suspend fun readBytes(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        activity.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
    }
}
