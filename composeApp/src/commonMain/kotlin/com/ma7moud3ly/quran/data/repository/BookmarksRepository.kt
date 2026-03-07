package com.ma7moud3ly.quran.data.repository


import com.ma7moud3ly.quran.model.Bookmark
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for interacting with the Bookmarks data.
 * This repository provides methods for saving, deleting, and retrieving reading Bookmarks entries.
 * It also offers a Flow to observe changes in the Bookmarks list.
 */
interface BookmarksRepository {
    suspend fun saveBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(bookmark: Bookmark)
    suspend fun getBookmark(id: String): Bookmark?
    suspend fun initBookmarks()
    val bookmarksFlow: Flow<List<Bookmark>>
}

