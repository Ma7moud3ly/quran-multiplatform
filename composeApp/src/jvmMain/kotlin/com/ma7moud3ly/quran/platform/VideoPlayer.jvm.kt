package com.ma7moud3ly.quran.platform

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.ma7moud3ly.quran.model.MyVideo
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    state: VideoPlayerState,
    video: () -> MyVideo
) {
    Image(
        painter = painterResource(video().thumbnail),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}