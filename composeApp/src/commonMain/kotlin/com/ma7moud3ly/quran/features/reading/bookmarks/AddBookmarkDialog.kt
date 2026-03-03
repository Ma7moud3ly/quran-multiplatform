package com.ma7moud3ly.quran.features.reading.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.ma7moud3ly.quran.features.recitation.config.verses.VerseSelectorDialog
import com.ma7moud3ly.quran.platform.Toast
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel
import com.ma7moud3ly.quran.resources.Res
import com.ma7moud3ly.quran.resources.bookmark_add
import com.ma7moud3ly.quran.resources.bookmark_added

@Composable
fun AddBookmarkDialog(
    viewModel: BookmarksViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val selectedChapter by viewModel.chapterFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    selectedChapter?.let { chapter ->
        var bookmarkVerseId by mutableStateOf(viewModel.selectedVerseId)
        VerseSelectorDialog(
            start = bookmarkVerseId,
            end = chapter.verses.last().id,
            limit = chapter.count,
            selectStart = true,
            showKeyboard = false,
            title = Res.string.bookmark_add,
            onConfirm = { newBookmarkVerseId, _ ->
                coroutineScope.launch {
                    viewModel.addBookmark(newBookmarkVerseId)
                    val message = getString(
                        Res.string.bookmark_added,
                        chapter.name,
                        newBookmarkVerseId
                    )
                    Toast.show(message)
                    onDismiss()
                }
            },
            onDismiss = onDismiss
        )
    }
}