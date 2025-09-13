package com.ma7moud3ly.quran.features.recitation.play

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.isAndroid
import com.ma7moud3ly.quran.ui.LocalPlatform
import kotlinx.coroutines.FlowPreview
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "PlaybackScreen"

@OptIn(FlowPreview::class)
@Composable
fun PlaybackScreen(
    viewModel: PlaybackViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSettings: () -> Unit
) {
    val platform = LocalPlatform.current
    val appSettings by viewModel.settingFlow.collectAsState()
    val mediaPlayerManager = remember { viewModel.getMediaPlayerManager() }

    LaunchedEffect(viewModel) {
        mediaPlayerManager.finishPlayback
            .collect { finish ->
                Log.v(TAG, "finishPlayback: $finish")
                if (finish) onBack()
            }
    }

    LifecycleResumeEffect(LocalLifecycleOwner) {
        viewModel.keepScreenOn(true)
        if (platform.isAndroid) mediaPlayerManager.resume()
        onPauseOrDispose {
            viewModel.keepScreenOn(false)
            if (platform.isAndroid &&
                !mediaPlayerManager.playInBackground
            ) {
                mediaPlayerManager.pause()
            }
        }
    }

    fun uiEvents(event: PlaybackEvents) {
        when (event) {
            is PlaybackEvents.Back -> onBack()
            is PlaybackEvents.OpenSettings -> onSettings()
        }
    }

    PlaybackScreenContent(enableReelMode = mediaPlayerManager.isReelMode) {
        if (mediaPlayerManager.isNormalScreenMode) {
            NormalPlayback(
                mediaPlayerManager = mediaPlayerManager,
                appSettings = { appSettings },
                uiEvents = ::uiEvents
            )
        } else {
            TvPlayback(
                slidesManager = viewModel.getSlidesManager(),
                mediaPlayerManager = mediaPlayerManager,
                uiEvents = ::uiEvents
            )
        }
    }
}