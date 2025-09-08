package com.ma7moud3ly.quran.features.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    reading: Boolean,
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val settings by viewModel.settingsFlow.collectAsState()
    val darkModeEnabled = remember { viewModel.darkModeFlow.value }
    val fonts = remember { viewModel.fontList }

    LaunchedEffect(Unit) {
        viewModel.getSettings(ofReading = reading)
    }

    settings?.let { settings ->
        SettingsScreenContent(
            darkModeEnabled = darkModeEnabled,
            reading = reading,
            settings = settings,
            fonts = fonts,
            uiEvents = {
                when (it) {
                    is SettingsEvents.OnBack -> {
                        onBack()
                    }

                    is SettingsEvents.ToggleDarkMode -> {
                        viewModel.toggleDarkMode()
                        onBack()
                    }

                    is SettingsEvents.SaveSettings -> {
                        viewModel.updateSettings(it.settings)
                        onBack()
                    }
                }
            }
        )
    }
}
