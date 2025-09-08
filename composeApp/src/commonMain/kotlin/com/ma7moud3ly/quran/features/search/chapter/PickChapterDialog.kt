package com.ma7moud3ly.quran.features.search.chapter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ma7moud3ly.quran.features.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PickChapterDialog(
    viewModel: HomeViewModel = koinViewModel(),
    selectedChapterId: Int = 1,
    onSelectChapter: (Int) -> Unit,
    onBack: () -> Unit
) {
    val chapters by viewModel.chaptersIndexFlow.collectAsState()

    PickChapterDialogContent(
        list = chapters,
        showKeyboard = false,
        selectedChapterId = selectedChapterId,
        onSelectChapter = onSelectChapter,
        onBack = onBack
    )
}