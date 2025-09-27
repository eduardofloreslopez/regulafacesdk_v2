package com.bungaedu.regulafacesdk_v2.data.model

data class Similarity(val score: Float) { // 0f..1f
    val percent: Int get() = (score * 100f).coerceIn(0f, 100f).toInt()
}
