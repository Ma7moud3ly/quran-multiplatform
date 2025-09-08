package com.ma7moud3ly.quran.features.settings

import com.ma7moud3ly.quran.model.AppSettings

sealed class SettingsEvents {
    data object ToggleDarkMode : SettingsEvents()
    data class SaveSettings(val settings: AppSettings) : SettingsEvents()
    data object OnBack : SettingsEvents()
}