package com.ma7moud3ly.quran.features.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ma7moud3ly.quran.model.History
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel(),
    onOpenHistory: (History) -> Unit,
    onBack: () -> Unit
) {
    val history by viewModel.historyFlow.collectAsState(listOf())

    HistoryScreenContent(
        list = history,
        onDeleteHistory = viewModel::deleteHistory,
        onOpenHistory = onOpenHistory,
        onBack = onBack
    )
}