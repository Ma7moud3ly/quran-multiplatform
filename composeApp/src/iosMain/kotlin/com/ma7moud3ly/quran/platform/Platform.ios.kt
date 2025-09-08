package com.ma7moud3ly.quran.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import platform.UIKit.UIDevice
import platform.UIKit.UIApplication
import platform.Foundation.NSUserDefaults
import com.russhwolf.settings.Settings
import com.russhwolf.settings.NSUserDefaultsSettings
import androidx.compose.runtime.Composable

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val id: String = "ios"
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun ioDispatcher(): CoroutineDispatcher = Dispatchers.Default

actual fun createSettings(): Settings {
    val userDefaults = NSUserDefaults.standardUserDefaults
    return NSUserDefaultsSettings(userDefaults)
}

actual fun platformKeepScreenOn(on: Boolean) {
    val application = UIApplication.sharedApplication
    application.idleTimerDisabled = on
}

@Composable
actual fun ShowFullScreen() {

}
