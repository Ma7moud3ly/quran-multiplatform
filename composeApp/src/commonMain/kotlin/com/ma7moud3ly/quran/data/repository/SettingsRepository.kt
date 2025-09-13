package com.ma7moud3ly.quran.data.repository


import com.ma7moud3ly.quran.model.AppFont
import com.ma7moud3ly.quran.model.ReadingSettings
import com.ma7moud3ly.quran.model.RecitationSettings
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.VersesMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing application settings.
 *
 * This interface defines methods for accessing and modifying various settings
 * related to the appearance of reading and recitation
 */
interface SettingsRepository {
    val darkModeFlow: Flow<Boolean>
    val darkMode: Boolean
    suspend fun toggleDarkMode()

    val readingFont: AppFont
    fun setReadingFont(value: AppFont)

    val recitationFont: AppFont
    fun setRecitationFont(value: AppFont)

    val fontList: List<AppFont>

    val readingVersesMode: VersesMode
    fun setReadingVersesMode(versesMode: VersesMode)

    val recitationVersesMode: VersesMode
    fun setRecitationVersesMode(versesMode: VersesMode)

    val tvSlide: String
    fun setTvSlide(value: String)

    fun getReadingSettings(): ReadingSettings
    suspend fun updateReadingSettings(settings: ReadingSettings)

    fun getRecitationSettings(): RecitationSettings
    suspend fun updateRecitationSettings(settings: RecitationSettings)

    val readingSettingsFlow: Flow<ReadingSettings>
    val recitationSettingsFlow: Flow<RecitationSettings>

    fun saveLastReciters(reciters: List<Reciter>)
    fun getLastReciterIds(): List<String>
}

