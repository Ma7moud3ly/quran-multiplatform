package com.ma7moud3ly.quran.data.impl

import com.ma7moud3ly.quran.data.repository.ChaptersRepository
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.SearchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import quran.composeapp.generated.resources.Res

@Single
class ChaptersRepositoryImpl(
    private val dispatcher: CoroutineDispatcher
) : ChaptersRepository {

    private val chaptersMap = mutableMapOf<Int, Chapter>()
    private var indexList: List<Chapter> = listOf()

    override suspend fun getIndex(): List<Chapter> = withContext(dispatcher) {
        try {
            if (indexList.isEmpty()) {
                val jsonString =
                    Res.readBytes("files/chapters/chapters_index.json").decodeToString()
                indexList = Json.decodeFromString<List<Chapter>>(jsonString)
            }
            indexList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun readChapter(chapterId: Int): Chapter? = withContext(dispatcher) {
        try {
            val jsonString = Res.readBytes("files/chapters/$chapterId.json").decodeToString()
            Json.decodeFromString<Chapter>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getChapter(chapterId: Int): Chapter? {
        return if (chaptersMap.containsKey(chapterId)) {
            chaptersMap[chapterId]
        } else {
            val chapter = readChapter(chapterId)
            if (chapter != null) {
                chaptersMap[chapterId] = chapter
            }
            chapter
        }
    }

    override suspend fun getChapters(ids: Set<Int>): List<Chapter> {
        return ids.mapNotNull { id -> getChapter(id) }
    }

    private suspend fun getChapters(): List<Chapter> {
        if (chaptersMap.size < 114) {
            for (i in 1..114) {
                if (chaptersMap.containsKey(i).not()) {
                    val chapter = readChapter(i)
                    if (chapter != null) {
                        chaptersMap[i] = chapter
                    }
                }
            }
        }
        return chaptersMap.values.toList()
    }

    override suspend fun searchChapters(query: String): Flow<List<Chapter>> = flow {
        val results = mutableListOf<Chapter>()
        if (query.isBlank()) {
            emit(results)
            return@flow
        }
        val query = query.trim()
        indexList.forEach { chapter ->
            if (chapter.name.contains(query)) results.add(chapter)
        }
        emit(results)
    }


    override suspend fun searchVerses(query: String): Flow<List<SearchResult>?> = flow {
        if (query.isBlank() || query.length < 3) {
            emit(null)
            return@flow
        }

        val searchResults = withContext(dispatcher) {
            val results = mutableListOf<SearchResult>()
            val chapters = getChapters() // This will run on `dispatcher`
            val queryNormalized = query.normalizeLetters()
            chapters.forEach { chapter ->
                chapter.verses.forEach { verse ->
                    if (verse.textNormalized.contains(queryNormalized)) {
                        val result = SearchResult(
                            chapterId = chapter.id,
                            chapterName = chapter.name,
                            verseId = verse.id,
                            content = verse.text,
                            selectionBegin = 0,
                            selectionEnd = 0
                        )
                        results.add(result)
                    }
                }
            }
            results
        }
        emit(searchResults)
    }.flowOn(dispatcher)

    private fun String.normalizeLetters(): String {
        return this
            .replace("إ", "ا")
            .replace("أ", "ا")
            .replace("آ", "ا")
            .replace("ؤ", "و")
            .replace("ئ", "ي")
            .replace("ى", "ي")
            .replace("ة", "ه")
            .replace("ـ", "")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}