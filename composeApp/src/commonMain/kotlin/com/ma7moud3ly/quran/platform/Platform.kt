package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher

interface Platform {
    val name: String
    val id: String
}

expect fun getPlatform(): Platform

val Platform.isWasmJs get() = this.id == "wasmJs"
val Platform.isJvm get() = this.id == "jvm"
val Platform.isAndroid get() = this.id == "android"
val Platform.isIos get() = this.id == "ios"
val Platform.isMobile get() = isAndroid || isIos


/**
 * Returns a coroutine dispatcher for I/O operations.
 * because the Dispatchers.IO may not be available on all platforms.
 */
expect fun ioDispatcher(): CoroutineDispatcher
/**
 * Initialize preferences settings across platforms.
 */

expect fun createSettings(): Settings

/**
 * Keeps the screen on when the app is in the foreground.
 * @param on true to keep the screen on, false otherwise.
 */
expect fun platformKeepScreenOn(on: Boolean)

@Composable
expect fun ShowFullScreen()