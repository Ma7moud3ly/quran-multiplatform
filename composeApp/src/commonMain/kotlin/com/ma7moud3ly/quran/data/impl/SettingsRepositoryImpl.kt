package com.ma7moud3ly.quran.data.impl

import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.data.repository.SettingsRepository
import com.ma7moud3ly.quran.managers.FontsManager
import com.ma7moud3ly.quran.model.AppFont
import com.ma7moud3ly.quran.model.PreferenceKeys
import com.ma7moud3ly.quran.model.PreferenceKeys.READING_VERSES_MODE
import com.ma7moud3ly.quran.model.PreferenceKeys.RECITATION_VERSES_MODE
import com.ma7moud3ly.quran.model.PreferenceKeys.TV_SLIDE_ID
import com.ma7moud3ly.quran.model.ReadingSettings
import com.ma7moud3ly.quran.model.RecitationSettings
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.VersesMode
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.Platform
import com.ma7moud3ly.quran.platform.isMobile
import com.ma7moud3ly.quran.platform.isWasmJs
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class SettingsRepositoryImpl(
    private val platformSettings: Settings,
    private val fontsManager: FontsManager,
    private val platform: Platform
) : SettingsRepository {

    companion object {
        private const val TAG = "SettingsRepository"
    }

    private val _darkModeFlow = MutableStateFlow(darkMode)
    override val darkModeFlow: Flow<Boolean> = _darkModeFlow.asStateFlow()

    override val darkMode: Boolean
        get() = platformSettings.getBoolean(
            PreferenceKeys.DARK_MODE,
            platform.isWasmJs.not()
        )

    override suspend fun toggleDarkMode() {
        val mode = darkMode.not()
        platformSettings.putBoolean(PreferenceKeys.DARK_MODE, mode)
        _darkModeFlow.emit(mode)
    }

    override val readingFont: AppFont
        get() {
            val id = platformSettings.getString(
                PreferenceKeys.READING_FONT_TYPE,
                FontsManager.DEFAULT_FONT
            )
            val size = platformSettings.getFloat(
                PreferenceKeys.READING_FONT_SIZE,
                30f
            ).sp

            val font = fontsManager.getFont(id).apply {
                fontSize = size
            }
            return font
        }

    override fun setReadingFont(value: AppFont) {
        platformSettings.putFloat(PreferenceKeys.READING_FONT_SIZE, value.fontSize.value)
        platformSettings.putString(PreferenceKeys.READING_FONT_TYPE, value.id)
    }

    override val recitationFont: AppFont
        get() {
            val id = platformSettings.getString(
                PreferenceKeys.RECITATION_FONT_TYPE,
                FontsManager.DEFAULT_FONT
            )
            val size = platformSettings.getFloat(
                PreferenceKeys.RECITATION_FONT_SIZE,
                30f
            ).sp

            val font = fontsManager.getFont(id).apply {
                fontSize = size
            }
            return font
        }

    override fun setRecitationFont(value: AppFont) {
        platformSettings.putFloat(PreferenceKeys.RECITATION_FONT_SIZE, value.fontSize.value)
        platformSettings.putString(PreferenceKeys.RECITATION_FONT_TYPE, value.id)
    }

    override val fontList: List<AppFont> get() = fontsManager.getFonts()

    override val readingVersesMode: VersesMode
        get() {
            val defaultReadingMode = if (platform.isMobile) 2 else 3
            val value = platformSettings.getInt(READING_VERSES_MODE, defaultReadingMode)
            return when (value) {
                1 -> VersesMode.Single
                2 -> VersesMode.Multiple
                3 -> VersesMode.Continues
                else -> VersesMode.Continues
            }
        }

    override fun setReadingVersesMode(versesMode: VersesMode) {
        val value = when (versesMode) {
            VersesMode.Single -> 1
            VersesMode.Multiple -> 2
            VersesMode.Continues -> 3
        }
        platformSettings.putInt(READING_VERSES_MODE, value)
    }

    override val recitationVersesMode: VersesMode
        get() {
            val value = platformSettings.getInt(
                RECITATION_VERSES_MODE, 1
            )
            return when (value) {
                1 -> VersesMode.Single
                else -> VersesMode.Multiple
            }
        }

    override fun setRecitationVersesMode(versesMode: VersesMode) {
        val value = when (versesMode) {
            VersesMode.Single -> 1
            else -> 2
        }
        platformSettings.putInt(RECITATION_VERSES_MODE, value)
    }


    override fun getReadingSettings(): ReadingSettings {
        return ReadingSettings(
            font = readingFont,
            versesMode = readingVersesMode
        )
    }

    override suspend fun updateReadingSettings(settings: ReadingSettings) {
        setReadingFont(settings.font)
        setReadingVersesMode(settings.versesMode)
        _readingsSettingsFlow.emit(settings)
    }

    override fun getRecitationSettings(): RecitationSettings {
        return RecitationSettings(
            font = recitationFont,
            versesMode = recitationVersesMode
        )
    }

    override suspend fun updateRecitationSettings(settings: RecitationSettings) {
        setRecitationFont(settings.font)
        setRecitationVersesMode(settings.versesMode)
        _recitationSettingsFlow.emit(settings)
    }

    private val _readingsSettingsFlow = MutableStateFlow(getReadingSettings())
    override val readingSettingsFlow: Flow<ReadingSettings> = _readingsSettingsFlow.asStateFlow()

    private val _recitationSettingsFlow = MutableStateFlow(getRecitationSettings())
    override val recitationSettingsFlow: Flow<RecitationSettings> =
        _recitationSettingsFlow.asStateFlow()

    override val tvSlide: String
        get() = platformSettings.getString(TV_SLIDE_ID, "")

    override fun setTvSlide(value: String) {
        platformSettings.putString(TV_SLIDE_ID, value)
    }

    override fun saveLastReciters(reciters: List<Reciter>) {
        val ids = reciters.joinToString(",") { it.id }
        Log.v(TAG, "saveLastReciters: $ids")
        platformSettings.putString(PreferenceKeys.RECITERS, ids)
    }

    override fun getLastReciterIds(): List<String> {
        val ids = platformSettings.getString(
            PreferenceKeys.RECITERS, ""
        ).split(",")
        Log.v(TAG, "getLastReciterIds: $ids")
        return ids
    }
}