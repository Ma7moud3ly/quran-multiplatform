package com.ma7moud3ly.quran.features.recitation.config.background

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun TvBackgroundDialog(
    viewModel: BackgroundsViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val backgrounds by viewModel.backgroundsFlow.collectAsState(listOf())
    val selectedBackground by remember { viewModel.selectedBackground }

    TvBackgroundContent(
        backgrounds = backgrounds,
        onDismiss = onDismiss,
        selectedBackground = { selectedBackground },
        onSelectBackground = {
            viewModel.selectBackground(it)
            onDismiss()
        },
        onAddNewBackground = viewModel::addNewBackground,
        onRemoveBackground = viewModel::removeBackground
    )
}