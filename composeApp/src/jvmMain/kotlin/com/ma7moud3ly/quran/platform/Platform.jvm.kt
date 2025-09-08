package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.prefs.Preferences

object AppConfig {
    val isDebug: Boolean by lazy {
        System.getProperty("app.build.mode") == "debug"
    }
}

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val id: String = "jvm"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

actual fun createSettings(): Settings {
    return PreferencesSettings(delegate = Preferences.userRoot())
}

actual fun platformKeepScreenOn(on: Boolean) {}

@Composable
actual fun ShowFullScreen() {
}