package com.ma7moud3ly.quran.model


data class SearchQuery(
    val query: String,
    val verses: Boolean,
    val chapters: Boolean,
    val reciters: Boolean
)

data class SearchResult(
    val chapterId: Int,
    val chapterName: String,
    val verseId: Int,
    val content: String,
    val selectionBegin: Int,
    val selectionEnd: Int
)

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class HasResult(val count: Int) : SearchState
}