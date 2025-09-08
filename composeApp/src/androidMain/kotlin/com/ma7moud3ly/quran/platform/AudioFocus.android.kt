package com.ma7moud3ly.quran.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class AudioFocus : AudioManager.OnAudioFocusChangeListener {
    private val audioManager = AndroidApp.INSTANCE.getSystemService(
        Context.AUDIO_SERVICE
    ) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null // For Android O and above
    private var hasAudioFocus = false
    private var playWhenFocusGained = false // To resume playback after temporary loss
    private val _audioFocusFlow = MutableStateFlow<AudioFocusEvents?>(null)
    actual val audioFocusFlow: Flow<AudioFocusEvents?> = _audioFocusFlow.asStateFlow()

    actual fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) return true
        val result: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(this@AudioFocus)
                build()
            }
            result = audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            @Suppress("DEPRECATION")
            result = audioManager.requestAudioFocus(
                this@AudioFocus,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        return if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            hasAudioFocus = true
            Log.v(TAG, "Audio focus granted")
            playWhenFocusGained = false
            true
        } else {
            hasAudioFocus = false
            Log.e(TAG, "Audio focus denied")
            false
        }
    }

    private fun abandonAudioFocus() {
        if (!hasAudioFocus) return

        Log.v(TAG, "Abandoning audio focus")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            audioFocusRequest = null
        } else {
            @Suppress("DEPRECATION")
            (audioManager.abandonAudioFocus(this@AudioFocus))
        }
        hasAudioFocus = false
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.v(TAG, "AUDIOFOCUS_GAIN")
                hasAudioFocus = true
                if (playWhenFocusGained) {
                    _audioFocusFlow.value = AudioFocusEvents.PLAY
                }
                playWhenFocusGained = false
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.v(TAG, "AUDIOFOCUS_LOSS")
                hasAudioFocus = false
                playWhenFocusGained = false // No need to resume
                _audioFocusFlow.value = AudioFocusEvents.STOP
                abandonAudioFocus() // Clean up
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.v(
                    TAG,
                    "AUDIOFOCUS_LOSS_TRANSIENT"
                )
                hasAudioFocus = false
                playWhenFocusGained = true
                _audioFocusFlow.value = AudioFocusEvents.PAUSE
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.v(
                    TAG,
                    "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"
                )
                playWhenFocusGained = true
                _audioFocusFlow.value = AudioFocusEvents.PAUSE
            }
        }
    }

    actual fun pause() {
        _audioFocusFlow.value = null
        playWhenFocusGained = true
    }

    actual fun release() {
        abandonAudioFocus()
        _audioFocusFlow.value = null
        playWhenFocusGained = false
    }

    companion object {
        private const val TAG = "AudioFocus"
    }
}