package com.ma7moud3ly.quran.platform

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.ma7moud3ly.quran.model.Video

@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    state: VideoPlayerState,
    video: () -> Video
) {
    Image(
        painter = video().getPainter(),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}