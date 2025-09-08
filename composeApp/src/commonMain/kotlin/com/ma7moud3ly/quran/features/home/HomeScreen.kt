package com.ma7moud3ly.quran.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    uiEvents: (HomeEvents) -> Unit
) {
    val chapters by viewModel.chaptersIndexFlow.collectAsState()
    val reciters by viewModel.recitersIndexFlow.collectAsState()

    HomeScreenContent(
        chapters = { chapters },
        reciters = { reciters },
        historyFlow = viewModel.historyFlow,
        onDeleteHistory = viewModel::deleteHistory,
        uiEvents = uiEvents
    )
}