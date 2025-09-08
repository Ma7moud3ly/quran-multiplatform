package com.ma7moud3ly.quran.platform

import com.ma7moud3ly.quran.model.MediaFile
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.Foundation.NSURL
import platform.darwin.NSObject

private const val TAG = "MediaPlayerController"

actual class MediaPlayer {

    private var audioPlayer: AVAudioPlayer? = null
    private var onComplete: (() -> Unit)? = null

    @OptIn(ExperimentalForeignApi::class)
    actual fun prepare(
        mediaFile: MediaFile,
        onPrepared: () -> Unit
    ) {
        release()

        val url = NSURL.fileURLWithPath(mediaFile.path)

        audioPlayer = try {
            AVAudioPlayer(contentsOfURL = url, fileTypeHint = null, error = null).apply {
                prepareToPlay()
                delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
                    override fun audioPlayerDidFinishPlaying(
                        player: AVAudioPlayer,
                        successfully: Boolean
                    ) {
                        handleCompletion()
                    }
                }
            }
        } catch (e: Throwable) {
            println("❌ Failed to create AVAudioPlayer: $e")
            null
        }
        onPrepared()
    }

    actual fun start() {
        audioPlayer?.play()
    }

    actual fun start(onCompletion: () -> Unit) {
        this.onComplete = onCompletion
        start()
    }

    private fun handleCompletion() {
        onComplete?.invoke()
    }

    actual fun pause() {
        audioPlayer?.pause()
    }

    actual fun release() {
        audioPlayer?.stop()
        audioPlayer = null
        onComplete = null
    }

    actual fun isPlaying(): Boolean = audioPlayer?.playing ?: false

    actual fun playInBackground() {}
    actual fun hideBackgroundNotification() {}
    actual fun releaseBackgroundService() {}
}

