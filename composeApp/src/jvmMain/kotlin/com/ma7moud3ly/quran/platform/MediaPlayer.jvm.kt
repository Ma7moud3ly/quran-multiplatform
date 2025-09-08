package com.ma7moud3ly.quran.platform

import com.ma7moud3ly.quran.model.MediaFile
import java.io.File
import java.net.URL
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

private const val TAG = "MediaPlayerController-JVM"

actual class MediaPlayer {
    private var clip: Clip? = null
    private var onComplete: (() -> Unit)? = null
    private var audioInputStream: AudioInputStream? = null

    actual fun prepare(
        mediaFile: MediaFile,
        onPrepared: () -> Unit
    ) {
        release()
        Log.v(TAG, "prepare")
        try {
            val audioSrc = if (mediaFile.exists) {
                File(mediaFile.path).toURI().toURL()
            } else {
                URL(mediaFile.url)
            }

            AudioSystem.getAudioInputStream(audioSrc).use { mp3Stream -> // Use-resource block
                // Define the target PCM format
                val baseFormat = mp3Stream.format
                val targetFormat = javax.sound.sampled.AudioFormat(
                    javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.sampleRate,
                    16, // 16-bit audio
                    baseFormat.channels,
                    baseFormat.channels * 2, // Frame size: channels * bytes_per_sample
                    baseFormat.sampleRate,
                    false // Big-endian: false (little-endian is common)
                )

                // Get a new stream that converts to PCM
                // This is the crucial step
                audioInputStream = AudioSystem.getAudioInputStream(targetFormat, mp3Stream)

                clip = AudioSystem.getClip()
                // Now open the clip with the PCM stream
                clip?.open(audioInputStream)

                clip?.addLineListener { event ->
                    Log.v(TAG, "line event ${event.type}")
                    if (event.type == LineEvent.Type.STOP) {
                        // Check if the clip stopped because it reached the end
                        val currentClip = event.line as? Clip
                        if (currentClip != null) {
                            // It's good practice to check for a small margin due to potential precision issues,
                            // though for Clips, position == length at the end is usually reliable.
                            val isEndOfMedia = currentClip.framePosition >= currentClip.frameLength
                            if (isEndOfMedia) {
                                Log.v(TAG, "Playback finished (reached end of media)")
                                onComplete?.invoke()
                            } else {
                                Log.v(
                                    TAG,
                                    "Playback stopped (manually or paused), not end of media."
                                )
                            }
                        }
                    }
                }
                onPrepared()
            }
        } catch (e: Exception) {
            // Log the full stack trace for better debugging
            println("Error preparing desktop audio: ${e.message}")
            e.printStackTrace() // Add this for more details
        }
    }

    actual fun start() {
        Log.v(TAG, "start")
        clip?.let {
            if (!it.isRunning) {
                it.framePosition = 0
                it.start()
            }
        }
    }

    actual fun start(onCompletion: () -> Unit) {
        onComplete = onCompletion
        start()
    }

    actual fun pause() {
        Log.v(TAG, "pause")
        clip?.stop() // For Clip, stop() is pause. Loop state is preserved.
    }

    actual fun release() {
        Log.v(TAG, "release")
        clip?.close()
        audioInputStream?.close()
        clip = null
        audioInputStream = null
    }

    actual fun isPlaying(): Boolean {
        return clip?.isRunning ?: false
    }

    actual fun playInBackground() {}
    actual fun hideBackgroundNotification() {}
    actual fun releaseBackgroundService() {}
}