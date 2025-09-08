package com.ma7moud3ly.quran.features.search

import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.SearchResult

sealed class SearchEvents {
    data class OpenVerse(val searchResult: SearchResult, val listen: Boolean) : SearchEvents()
    data class OpenChapter(val chapterId: Int, val listen: Boolean = false) : SearchEvents()
    data class OpenReciter(val reciter: Reciter) : SearchEvents()
    object OnBack : SearchEvents()
}