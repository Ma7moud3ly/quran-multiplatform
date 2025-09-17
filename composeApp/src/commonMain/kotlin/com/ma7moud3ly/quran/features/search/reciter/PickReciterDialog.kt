package com.ma7moud3ly.quran.features.search.reciter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ma7moud3ly.quran.features.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PickReciterDialog(
    viewModel: HomeViewModel = koinViewModel(),
    selectedReciterIds: List<String>,
    selectMultiple: Boolean,
    onSelectReciter: (List<String>) -> Unit,
    onBack: () -> Unit
) {
    val allReciters by viewModel.recitersIndexFlow.collectAsState()

    PickReciterDialogContent(
        allReciters = allReciters,
        selectMultiple = selectMultiple,
        selectedReciterIds = selectedReciterIds,
        onSelectReciter = onSelectReciter,
        onBack = onBack
    )
}