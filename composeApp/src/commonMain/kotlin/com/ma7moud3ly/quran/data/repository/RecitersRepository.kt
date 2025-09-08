package com.ma7moud3ly.quran.data.repository

import com.ma7moud3ly.quran.model.Reciter
import kotlinx.coroutines.flow.Flow


/**
 * Interface defining the contract for accessing and managing Quran reciter data.
 * This repository provides methods to retrieve reciter information, such as lists of reciters,
 * individual reciters by ID, and search functionality.
 */
interface RecitersRepository {
    suspend fun getRecitersIndex(): List<Reciter>
    suspend fun getReciter(reciterId: String): Reciter?
    suspend fun getReciters(ids: List<String>): List<Reciter>
    suspend fun searchReciters(query: String): Flow<List<Reciter>?>
}