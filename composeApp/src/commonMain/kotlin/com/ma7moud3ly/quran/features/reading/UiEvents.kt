package com.ma7moud3ly.quran.features.reading

sealed interface ReadingEvents {
    data object Back : ReadingEvents
    data object OpenSettings : ReadingEvents
    data class AddBookmark(val chapterId: Int, val verseId: Int) : ReadingEvents
    data class PlayVerse(val chapterId: Int, val verseId: Int) : ReadingEvents
    data class NextChapter(val chapterId: Int) : ReadingEvents
    data class PreviousChapter(val chapterId: Int) : ReadingEvents
}