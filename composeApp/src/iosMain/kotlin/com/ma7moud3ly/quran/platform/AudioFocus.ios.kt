package com.ma7moud3ly.quran.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class AudioFocus {
    actual fun requestAudioFocus(): Boolean {
        return true
    }

    actual fun pause() {
    }

    actual fun release() {
    }

    actual val audioFocusFlow: Flow<AudioFocusEvents?> get() = flow { null }
}