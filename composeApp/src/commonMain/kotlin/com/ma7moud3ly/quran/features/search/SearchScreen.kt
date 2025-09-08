package com.ma7moud3ly.quran.features.search

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    uiEvents: (SearchEvents) -> Unit
) {
    SearchScreenContent(
        versesFlow = viewModel.versesResult,
        chaptersFlow = viewModel.chaptersResult,
        recitersFlow = viewModel.recitersResult,
        onSearch = viewModel::search,
        uiEvents = uiEvents
    )
}