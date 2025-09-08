package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ma7moud3ly.quran.model.MyVideo


/**
 * Composable function to display a video player.
 * This video player is used to display a video at the background of TV playback mode.
 */
@Composable
expect fun VideoPlayer(
    modifier: Modifier = Modifier,
    state: VideoPlayerState,
    video: () -> MyVideo
)

data class VideoPlayerState(val playing: Boolean) {
    var play: () -> Unit = {}
    var pause: () -> Unit = {}
}

@Composable
fun rememberVideoPlayerState(
    playing: Boolean = true
) = remember {
    VideoPlayerState(
        playing = playing
    )
}