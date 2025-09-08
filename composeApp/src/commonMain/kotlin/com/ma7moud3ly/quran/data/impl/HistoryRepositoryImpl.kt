package com.ma7moud3ly.quran.data.impl

import com.ma7moud3ly.quran.data.repository.HistoryRepository
import com.ma7moud3ly.quran.model.History
import com.ma7moud3ly.quran.platform.Log
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import org.koin.core.annotation.Single

@Single
class HistoryRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val settings: Settings,
) : HistoryRepository {
    private val historyMap = mutableMapOf<String, History>()
    private val key = "history_list"

    companion object {
        private const val TAG = "HistoryRepository"
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private fun saveHistoryMap() {
        settings.encodeValue(
            serializer = ListSerializer(History.serializer()),
            value = historyMap.values.toList(),
            key = key
        )
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    private suspend fun getHistory(): List<History> = withContext(dispatcher) {
        if (historyMap.isEmpty()) {
            val historyList: List<History> = settings.decodeValue(
                serializer = ListSerializer(History.serializer()),
                key = key,
                defaultValue = emptyList()
            )
            historyMap.putAll(historyList.associateBy { it.id })
        }
        historyMap.values.toList().sortedByDescending { it.timeStamp }
    }

    override suspend fun saveHistory(history: History) {
        Log.v(TAG," saveHistory: $history")
        withContext(dispatcher) {
            historyMap[history.id] = history
            saveHistoryMap()
            _historyFlow.emit(getHistory())
        }
    }

    override suspend fun deleteHistory(history: History) {
        Log.v(TAG," deleteHistory: $history")
        withContext(dispatcher) {
            historyMap.remove(history.id)
            saveHistoryMap()
            _historyFlow.emit(getHistory())
        }
    }

    override suspend fun getHistory(id: String): History? {
        if (historyMap.isEmpty()) getHistory()
        return historyMap[id]
    }

    override suspend fun initHistory() {
        _historyFlow.emit(getHistory())
    }

    private val _historyFlow = MutableStateFlow(historyMap.values.toList())
    override val historyFlow: Flow<List<History>> = _historyFlow.asStateFlow()

}