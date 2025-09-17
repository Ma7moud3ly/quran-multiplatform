package com.ma7moud3ly.quran.model


data class SearchQuery(
    val query: String,
    val verses: Boolean,
    val chapters: Boolean,
    val reciters: Boolean
) {
    val noSource: Boolean = !verses && !chapters && !reciters
}

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

data class SearchStateTrigger(
    val verses: List<SearchResult>?,
    val chapters: List<Chapter>?,
    val reciters: List<Reciter>?,
    val includeVerses: Boolean,
    val includeChapters: Boolean,
    val includeReciters: Boolean
)

