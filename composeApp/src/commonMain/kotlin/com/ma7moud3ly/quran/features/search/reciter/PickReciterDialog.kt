package com.ma7moud3ly.quran.features.search.reciter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.features.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PickReciterDialog(
    viewModel: HomeViewModel = koinViewModel(),
    selectedReciterId: String,
    filterReciters: Boolean,
    onSelectReciter: (Reciter) -> Unit,
    onBack: () -> Unit
) {
    val reciters by viewModel.recitersIndexFlow.collectAsState()

    PickReciterDialogContent(
        list = reciters,
        filterReciters = filterReciters,
        selectedReciterId = selectedReciterId,
        onSelectReciter = onSelectReciter,
        onBack = onBack
    )
}