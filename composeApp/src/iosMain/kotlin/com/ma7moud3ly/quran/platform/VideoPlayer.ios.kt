package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ma7moud3ly.quran.model.MyVideo
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.Foundation.NSURL
import platform.UIKit.UIViewController

private const val TAG = "VideoPlayer"

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    modifier: Modifier,
    state: VideoPlayerState,
    video: () -> MyVideo
) {
    val currentVideo = video()
    val player: AVPlayer = remember(currentVideo) {
        AVPlayer(uRL = NSURL.fileURLWithPath(currentVideo.path))
    }
    // Attach control functions to state
    LaunchedEffect(Unit) {
        state.play = {
            player.play()
            Log.v(TAG,"play")
        }
        state.pause = {
            player.pause()
            Log.v(TAG,"pause")
        }
    }

    LifecycleResumeEffect(LocalLifecycleOwner) {
        player.play()
        Log.v(TAG,"LifecycleResumeEffect-play")
        onPauseOrDispose {
            player.pause()
            Log.v(TAG,"LifecycleResumeEffect-pause")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.pause()
            Log.v(TAG,"release")
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            val controller = UIViewController()

            val playerLayer = AVPlayerLayer().apply {
                this.player = player
                videoGravity = AVLayerVideoGravityResizeAspectFill // similar to RESIZE_MODE_ZOOM
                frame = controller.view.bounds
            }

            controller.view.layer.addSublayer(playerLayer)
            controller.view
        },
        update = { view ->
            // Keep layer sizing in sync
            (view.layer.sublayers?.firstOrNull() as? AVPlayerLayer)?.frame = view.bounds
        }
    )
}
