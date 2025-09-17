package com.ma7moud3ly.quran.data.impl

import com.ma7moud3ly.quran.data.repository.RecitersRepository
import com.ma7moud3ly.quran.model.Reciter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import quran.composeapp.generated.resources.Res

@Single
class RecitersRepositoryImpl(
    private val dispatcher: CoroutineDispatcher
) : RecitersRepository {

    companion object {
        private var recitersMap: Map<String, Reciter> = mapOf()
    }

    override suspend fun getReciter(reciterId: String): Reciter? = withContext(dispatcher) {
        recitersMap[reciterId]
    }

    override suspend fun getReciters(ids: List<String>): List<Reciter> {
        val reciters = mutableListOf<Reciter>()
        ids.forEach { id ->
            recitersMap[id]?.let { reciters.add(it) }
        }
        return reciters
    }

    override suspend fun getRecitersIndex(): List<Reciter> = withContext(dispatcher) {
        try {
            if (recitersMap.isEmpty()) {
                val jsonString = Res.readBytes("files/reciters/reciters.json").decodeToString()
                val list = Json.decodeFromString<List<Reciter>>(jsonString)
                recitersMap = list.associateBy { it.id }
            }
            recitersMap.values.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun searchReciters(query: String): Flow<List<Reciter>?> = flow {
        val query = query.trim()
        if (query.isEmpty() || query.length < 3) {
            emit(null)
            return@flow
        }
        val results = mutableListOf<Reciter>()
        recitersMap.values.forEach { reciter ->
            if (reciter.name.contains(query)) results.add(reciter)
        }
        emit(results)
    }
}