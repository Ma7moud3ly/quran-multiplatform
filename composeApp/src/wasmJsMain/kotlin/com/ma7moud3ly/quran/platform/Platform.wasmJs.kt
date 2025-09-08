package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val id: String = "wasmJs"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.Default // Standard for Wasm JS

actual fun createSettings(): Settings {
    return StorageSettings()
}

actual fun platformKeepScreenOn(on: Boolean) {}

@Composable
actual fun ShowFullScreen() {
}