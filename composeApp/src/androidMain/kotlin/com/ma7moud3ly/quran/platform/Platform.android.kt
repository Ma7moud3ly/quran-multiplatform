package com.ma7moud3ly.quran.platform

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object AndroidApp {
    private var INSTANCE: Application? = null
    lateinit var window: Window
    fun init(application: Application) {
        this.INSTANCE = application
    }

    fun getContext(): Context? = INSTANCE
    fun requireContext() = INSTANCE ?: throw IllegalStateException("Context not initialized")
}

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val id: String = "android"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

actual fun createSettings(): Settings {
    val context = AndroidApp.requireContext()
    return SharedPreferencesSettings(
        context.getSharedPreferences(
            "app_settings",
            Context.MODE_PRIVATE
        )
    )
}

actual fun platformKeepScreenOn(on: Boolean) {
    if (on) {
        AndroidApp.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        AndroidApp.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
actual fun ShowFullScreen() {
    val context = LocalContext.current
    val window = (context as Activity).window
    DisposableEffect(Unit) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide system bars
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        onDispose {
            // Show system bars when the Composable leaves the composition
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}