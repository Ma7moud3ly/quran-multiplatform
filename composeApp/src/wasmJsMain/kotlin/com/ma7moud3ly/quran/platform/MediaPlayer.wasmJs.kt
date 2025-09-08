package com.ma7moud3ly.quran.platform

import com.ma7moud3ly.quran.model.MediaFile
import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

actual class MediaPlayer {
    private var onComplete: (() -> Unit)? = null
    private var audioElement: HTMLAudioElement? = null
    private var isPrepared = false

    // ... (prepare, other methods) ...
    actual fun prepare(
        mediaFile: MediaFile,
        onPrepared: () -> Unit
    ) {
        Log.v(TAG, "prepare ${mediaFile.url}")

        release()
        isPrepared = false
        audioElement = document.createElement("audio") as HTMLAudioElement
        audioElement?.src = mediaFile.url
        audioElement?.preload = "auto"
        audioElement?.oncanplaythrough = {
            if (isPrepared.not()) {
                isPrepared = true
                onPrepared()
            }
        }
        audioElement?.onended = { handlePlaybackEnded() }
    }

    private fun handlePlaybackEnded() {
        Log.v(TAG, "handlePlaybackEnded")
        onComplete?.invoke()
    }

    actual fun start() {
        if (isPrepared) {
            Log.v(TAG, "start")
            audioElement?.play()
        } else {
            Log.v(TAG, "failed to start")
        }
    }

    actual fun start(onCompletion: () -> Unit) {
        this.onComplete = onCompletion
        start()
    }


    actual fun pause() {
        Log.v(TAG, "pause")
        audioElement?.pause()
    }

    actual fun release() {
        Log.v(TAG, "release")
        audioElement?.onended = null // Remove listener
        audioElement?.pause()
        audioElement?.src = ""
        audioElement?.removeAttribute("src")
        audioElement?.load()
        audioElement = null
        isPrepared = false
    }

    actual fun isPlaying(): Boolean {
        return audioElement?.let { !it.paused && !it.ended && it.readyState > 2 } ?: false
    }

    actual fun playInBackground() {}
    actual fun hideBackgroundNotification() {}
    actual fun releaseBackgroundService() {}

    companion object {
        private const val TAG = "MediaPlayerController-wasmJs"
    }
}

