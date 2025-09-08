package com.ma7moud3ly.quran.data.repository


import com.ma7moud3ly.quran.model.History
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for interacting with the history data.
 * This repository provides methods for saving, deleting, and retrieving reading history entries.
 * It also offers a Flow to observe changes in the history list.
 */
interface HistoryRepository {
    suspend fun saveHistory(history: History)
    suspend fun deleteHistory(history: History)
    suspend fun getHistory(id: String): History?
    suspend fun initHistory()
    val historyFlow: Flow<List<History>>
}

