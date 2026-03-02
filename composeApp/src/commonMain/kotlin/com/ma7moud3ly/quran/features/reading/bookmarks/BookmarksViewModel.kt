package com.ma7moud3ly.quran.features.reading.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma7moud3ly.quran.data.repository.BookmarksRepository
import com.ma7moud3ly.quran.model.Bookmark
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BookmarksViewModel(
    private val bookmarksRepository: BookmarksRepository
) : ViewModel() {
    fun addBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarksRepository.saveBookmark(bookmark)
        }
    }
}