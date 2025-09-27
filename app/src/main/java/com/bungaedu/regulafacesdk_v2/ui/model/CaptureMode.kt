package com.bungaedu.regulafacesdk_v2.ui.model

/**
 * Representa los modos de captura facial soportados por el SDK.
 *
 * Se utiliza para indicar al [FaceCaptureLauncher] qué tipo de proceso
 * de captura debe iniciarse.
 */
enum class CaptureMode {

    /**
     * Captura pasiva: el usuario solo necesita mirar a la cámara.
     */
    PASSIVE,

    /**
     * Captura activa: incluye comprobaciones de "liveness" (ej. movimientos, parpadeo).
     */
    ACTIVE
}

