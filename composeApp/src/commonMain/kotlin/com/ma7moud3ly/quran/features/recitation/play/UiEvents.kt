package com.ma7moud3ly.quran.features.recitation.play

sealed interface PlaybackEvents {
    object Back : PlaybackEvents
    object OpenSettings : PlaybackEvents
}