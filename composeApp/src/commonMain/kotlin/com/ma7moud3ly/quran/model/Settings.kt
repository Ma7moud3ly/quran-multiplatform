package com.ma7moud3ly.quran.model

import com.ma7moud3ly.quran.managers.FontsManager

interface AppSettings {
    val font: AppFont
    val versesMode: VersesMode
}

data class ReadingSettings(
    override val font: AppFont = FontsManager.first(),
    override val versesMode: VersesMode = VersesMode.Continues,
) : AppSettings

data class RecitationSettings(
    override val font: AppFont = FontsManager.first(),
    override val versesMode: VersesMode = VersesMode.Multiple,
    var tvSlide: Int = 0,
) : AppSettings