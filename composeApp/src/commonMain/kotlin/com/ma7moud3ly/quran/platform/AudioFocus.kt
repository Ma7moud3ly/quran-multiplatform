package com.ma7moud3ly.quran.platform

import kotlinx.coroutines.flow.Flow
/**
 * The main function of this class is to ensure that the application properly handles audio focus
 * when interacting with other audio-playing applications on the device. This helps to provide
 * a seamless and user-friendly audio experience.
 *
 */
expect class AudioFocus() {
    val audioFocusFlow: Flow<AudioFocusEvents?>
    fun requestAudioFocus(): Boolean
    fun pause()
    fun release()
}
/**
 * An enumeration of audio focus events that can occur.
 */
enum class AudioFocusEvents {
    PLAY,
    PAUSE,
    STOP
}