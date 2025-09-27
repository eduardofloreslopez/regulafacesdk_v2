package com.bungaedu.regulafacesdk_v2.data.model

/**
 * Representa una imagen facial en memoria como un array de bytes.
 *
 * @property bytes Contenido binario de la imagen en formato estándar (p. ej., JPEG o PNG).
 */
@JvmInline
value class FaceImage(val bytes: ByteArray)
