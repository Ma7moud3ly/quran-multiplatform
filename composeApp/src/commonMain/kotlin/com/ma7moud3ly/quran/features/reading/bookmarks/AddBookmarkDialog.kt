package com.ma7moud3ly.quran.features.reading.bookmarks

import androidx.compose.runtime.Composable
import com.ma7moud3ly.quran.model.Bookmark
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AddBookmarkDialog(
    chapterId: Int,
    verseId: Int,
    viewModel: BookmarksViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    AddBookmarkContent(
        chapterId = chapterId,
        verseId = verseId,
        onConfirm = {
            val bookmark = Bookmark(
                chapterId = chapterId,
                verseId = verseId,

                )
            viewModel.addBookmark(bookmark)
            onDismiss()
        },
        onDismiss = onDismiss
    )
}