package com.ma7moud3ly.quran.data.repository

import androidx.compose.runtime.MutableState
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.model.RecitationState
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing recitation data.
 * This interface defines the contract for accessing and modifying recitation information,
 * including the current recitation, data source selection, and background playback status.
 */
interface RecitationRepository {
    val recitationFlow: Flow<Recitation?>
    val recitationState: MutableState<RecitationState>
    fun getRecitation(): Recitation
    fun setRecitation(recitation: Recitation) {}
    fun setOnlineDataSource() {}
    fun setLocalDataSourceOnline() {}
    fun isPlayInBackground(): Boolean = false
}