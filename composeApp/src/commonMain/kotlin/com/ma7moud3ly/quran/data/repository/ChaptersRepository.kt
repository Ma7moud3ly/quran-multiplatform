package com.ma7moud3ly.quran.data.repository

import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.SearchResult
import kotlinx.coroutines.flow.Flow


/**
 * Repository interface for accessing chapter and verse data from the Quran.
 * Provides methods for retrieving chapter information, searching for verses,
 * and searching for chapters.
 */
interface ChaptersRepository {
    suspend fun getIndex(): List<Chapter>
    suspend fun getChapter(chapterId: Int): Chapter?
    suspend fun getChapters(ids: Set<Int>): List<Chapter>
    suspend fun searchVerses(query: String): Flow<List<SearchResult>?>
    suspend fun searchChapters(query: String): Flow<List<Chapter>?>
}