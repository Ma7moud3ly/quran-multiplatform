package com.ma7moud3ly.quran.features.home

import com.ma7moud3ly.quran.model.Bookmark
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.Reciter

sealed interface HomeEvents {
    data object Back : HomeEvents
    data object Search : HomeEvents
    data object OpenHistory : HomeEvents
    data class OpenChapter(val chapter: Chapter) : HomeEvents
    data class PlayChapter(val chapter: Chapter) : HomeEvents
    data class OpenReciter(val reciter: Reciter) : HomeEvents
    data class OpenSettings(val reading: Boolean) : HomeEvents
    data class OpenBookmark(val bookmark: Bookmark) : HomeEvents
}