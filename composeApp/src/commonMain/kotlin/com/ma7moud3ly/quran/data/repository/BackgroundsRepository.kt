package com.ma7moud3ly.quran.data.repository

import androidx.compose.runtime.MutableState
import com.ma7moud3ly.quran.model.TvBackground
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for accessing and managing TV slides.
 * This repository provides methods to retrieve all slides or a specific slide by its ID.
 */
interface BackgroundsRepository {
    val backgroundsFlow: Flow<List<TvBackground>>
    val selectedBackground: MutableState<TvBackground>
    suspend fun initBackgrounds()
    fun getBackgrounds(): List<TvBackground>
    fun getSelectedBackground(): TvBackground
    fun selectBackground(tvBackground: TvBackground)
    suspend fun removeBackground(tvBackground: TvBackground)
    suspend fun addNewBackground()
}