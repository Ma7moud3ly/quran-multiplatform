package com.ma7moud3ly.quran

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.ma7moud3ly.quran.features.settings.SettingsViewModel
import com.ma7moud3ly.quran.ui.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavHostReady: suspend (NavController) -> Unit = {},
    openPlaybackScreen: Boolean = false
) {
    val darkTheme by viewModel.darkModeFlow.collectAsState()
    AppTheme(darkTheme = darkTheme) {
        AppGraph(
            openPlaybackScreen = openPlaybackScreen,
            onNavHostReady = onNavHostReady
        )
    }
}