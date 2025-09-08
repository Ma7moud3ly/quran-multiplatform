package com.ma7moud3ly.quran.features.reading

sealed interface ReadingEvents {
    object Back : ReadingEvents
    object OpenSettings : ReadingEvents
    data class PlayVerse(val verseId: Int) : ReadingEvents
    object NextChapter : ReadingEvents
    object PreviousChapter : ReadingEvents
}