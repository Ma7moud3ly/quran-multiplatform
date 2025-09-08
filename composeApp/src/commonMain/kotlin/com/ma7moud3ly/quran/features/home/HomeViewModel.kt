package com.ma7moud3ly.quran.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma7moud3ly.quran.data.repository.ChaptersRepository
import com.ma7moud3ly.quran.data.repository.HistoryRepository
import com.ma7moud3ly.quran.data.repository.RecitersRepository
import com.ma7moud3ly.quran.model.History
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val chaptersRepository: ChaptersRepository,
    private val recitersRepository: RecitersRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            historyRepository.initHistory()
        }
    }

    val historyFlow = historyRepository.historyFlow

    fun deleteHistory(history: History) {
        viewModelScope.launch {
            historyRepository.deleteHistory(history)
        }
    }

    val chaptersIndexFlow = flow { emit(chaptersRepository.getIndex()) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val recitersIndexFlow = flow { emit(recitersRepository.getRecitersIndex()) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


}