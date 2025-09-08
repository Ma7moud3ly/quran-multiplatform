package com.ma7moud3ly.quran.model

data class DownloadResult(
    val id: String,
    val success: Boolean
)

data class DownloadProgress(
    val downloaded: Double = 0.0,
    val size: Double = 0.0,
    val percent: Float = 0f
)