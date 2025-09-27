package com.bungaedu.regulafacesdk_v2.data.model

/**
 * Representa el resultado de una comparación facial.
 *
 * @property score Valor de similitud normalizado en el rango [0.0, 1.0].
 *                 - 0.0 → ninguna coincidencia.
 *                 - 1.0 → coincidencia perfecta.
 */
data class Similarity(val score: Float) { // 0f..1f
    val percent: Int get() = (score * 100f).coerceIn(0f, 100f).toInt()
}
