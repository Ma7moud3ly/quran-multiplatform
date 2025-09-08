package com.ma7moud3ly.quran.model

data class MediaFile(
    val path: String,
    val exists: Boolean,
    val url: String = ""
)