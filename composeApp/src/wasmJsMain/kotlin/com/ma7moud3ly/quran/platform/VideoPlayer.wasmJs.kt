package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.WebElementView
import com.ma7moud3ly.quran.model.Video
import kotlinx.browser.document
import org.w3c.dom.HTMLVideoElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    state: VideoPlayerState,
    video: () -> Video
) {
    val video = video()
    WebElementView(
        factory = {
            (document.createElement("video")
                    as HTMLVideoElement)
                .apply {
                    src = video.path
                    controls = false
                    autoplay = true
                    loop = true
                    muted = true
                    style.width = "100%"
                    style.height = "100%"
                    style.objectFit = "cover"
                    style.objectPosition = "center"
                    style.zIndex = "-1"
                }
        },
        modifier = modifier,
        update = { it.src = video.path }
    )
}