package com.ma7moud3ly.quran.data.impl

import com.ma7moud3ly.quran.data.repository.BookmarksRepository
import com.ma7moud3ly.quran.model.Bookmark
import com.ma7moud3ly.quran.platform.Log
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import org.koin.core.annotation.Single

@Single
class BookmarksRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val settings: Settings,
) : BookmarksRepository {
    private val bookmarksMap = mutableMapOf<String, Bookmark>()
    private val key = "Bookmarks_list"

    companion object {
        private const val TAG = "BookmarksRepository"
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private fun saveBookmarksMap() {
        settings.encodeValue(
            serializer = ListSerializer(Bookmark.serializer()),
            value = bookmarksMap.values.toList(),
            key = key
        )
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private suspend fun getBookmarks(): List<Bookmark> = withContext(dispatcher) {
        if (bookmarksMap.isEmpty()) {
            val bookmarksList: List<Bookmark> = settings.decodeValue(
                serializer = ListSerializer(Bookmark.serializer()),
                key = key,
                defaultValue = emptyList()
            )
            bookmarksMap.putAll(bookmarksList.associateBy { it.id })
        }
        bookmarksMap.values.toList().sortedByDescending { it.timeStamp }
    }

    override suspend fun saveBookmark(bookmark: Bookmark) {
        Log.v(TAG, " saveBookmark: $bookmark")
        withContext(dispatcher) {
            bookmarksMap[bookmark.id] = bookmark
            saveBookmarksMap()
            _bookmarkFlow.emit(getBookmarks())
        }
    }

    override suspend fun deleteBookmark(bookmark: Bookmark) {
        Log.v(TAG, " deleteBookmark: $bookmark")
        withContext(dispatcher) {
            bookmarksMap.remove(bookmark.id)
            saveBookmarksMap()
            _bookmarkFlow.emit(getBookmarks())
        }
    }

    override suspend fun getBookmark(id: String): Bookmark? {
        if (bookmarksMap.isEmpty()) getBookmarks()
        return bookmarksMap[id]
    }

    override suspend fun initBookmarks() {
        _bookmarkFlow.emit(getBookmarks())
    }

    private val _bookmarkFlow = MutableStateFlow(bookmarksMap.values.toList())
    override val bookmarksFlow: Flow<List<Bookmark>> = _bookmarkFlow.asStateFlow()

}