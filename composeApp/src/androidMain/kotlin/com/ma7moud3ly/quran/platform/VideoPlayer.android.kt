package com.ma7moud3ly.quran.platform

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.ma7moud3ly.quran.model.MyVideo

private const val TAG = "VideoPlayer"

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    state: VideoPlayerState,
    video: () -> MyVideo
) {
    val context = LocalContext.current
    val currentVideo = video()
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }

    LaunchedEffect(currentVideo, exoPlayer) {
        val item = MediaItem.fromUri(currentVideo.path.toUri())
        exoPlayer.setMediaItem(item)
        Log.v(TAG,"play = ${currentVideo.path}")
    }

    LaunchedEffect(Unit) {
        state.play = {
            exoPlayer.play()
            Log.v(TAG,"play")
        }
        state.pause = {
            exoPlayer.pause()
            Log.v(TAG,"pause")
        }
    }

    LifecycleResumeEffect(LocalLifecycleOwner) {
        exoPlayer.play()
        Log.v(TAG,"LifecycleResumeEffect-play")
        onPauseOrDispose {
            exoPlayer.pause()
            Log.v(TAG,"LifecycleResumeEffect-pause")
        }
    }

    DisposableEffect(LocalLifecycleOwner) {
        onDispose {
            exoPlayer.release()
            Log.v(TAG,"release")
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                controllerAutoShow = false
                controllerHideOnTouch = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        }, update = { playerView ->
            playerView.useController = false
            playerView.controllerAutoShow = false
        },
        modifier = modifier
    )
}

