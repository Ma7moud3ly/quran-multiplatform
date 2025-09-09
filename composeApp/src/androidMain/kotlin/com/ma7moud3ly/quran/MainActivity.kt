package com.ma7moud3ly.quran

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ma7moud3ly.quran.features.settings.SettingsViewModel
import com.ma7moud3ly.quran.platform.AndroidApp
import com.ma7moud3ly.quran.ui.themeColors
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val openPlaybackScreen = intent.getBooleanExtra(PlaybackService.OPEN_PLAY_BACK, false)
        setContent {
            val viewModel: SettingsViewModel = koinViewModel()
            ConfigureSystemBars(viewModel = viewModel)
            App(
                viewModel = viewModel,
                openPlaybackScreen = openPlaybackScreen
            )
        }
        AndroidApp.window = window
    }
}

@Suppress("DEPRECATION")
@Composable
private fun ConfigureSystemBars(viewModel: SettingsViewModel) {
    val darkTheme by viewModel.darkModeFlow.collectAsState()
    val systemBarsColor = themeColors(darkTheme).background.toArgb()

    val view = LocalView.current
    val window = LocalActivity.current?.window
    if (view.isInEditMode.not() && window != null) {
        SideEffect {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = systemBarsColor
                window.navigationBarColor = systemBarsColor
            }
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = darkTheme.not()
            windowInsetsController.isAppearanceLightNavigationBars = darkTheme.not()
        }
    }
}


