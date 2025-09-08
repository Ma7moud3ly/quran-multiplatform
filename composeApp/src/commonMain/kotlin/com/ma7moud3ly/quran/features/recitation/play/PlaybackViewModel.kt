package com.ma7moud3ly.quran.features.recitation.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma7moud3ly.quran.data.repository.HistoryRepository
import com.ma7moud3ly.quran.data.repository.RecitationRepository
import com.ma7moud3ly.quran.data.repository.SettingsRepository
import com.ma7moud3ly.quran.data.repository.SlidesRepository
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.model.History
import com.ma7moud3ly.quran.model.ScreenMode
import com.ma7moud3ly.quran.model.RecitationSettings
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.platformKeepScreenOn
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@OptIn(FlowPreview::class)
@KoinViewModel
class PlaybackViewModel(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val slidesRepository: SlidesRepository,
    private val recitationRepository: RecitationRepository,
    private val mediaPlayerManager: MediaPlayerManager
) : ViewModel() {

    fun getMediaPlayerManager() = mediaPlayerManager

    init {
        mediaPlayerManager.initPlayBack()
    }

    override fun onCleared() {
        super.onCleared()
        Log.v(TAG, "onCleared")
        saveHistory()
        if (mediaPlayerManager.playInBackground.not()) mediaPlayerManager.release()
    }

    val settingFlow: StateFlow<RecitationSettings> = settingsRepository.recitationSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = settingsRepository.getRecitationSettings()
        )


    fun setTvSlide(index: Int) {
        viewModelScope.launch {
            settingsRepository.setTvSlide(index)
        }
    }

    fun keepScreenOn(on: Boolean) {
        platformKeepScreenOn(on)
    }

    private fun saveHistory() {
        Log.v(TAG, "saveHistory")
        viewModelScope.launch(NonCancellable) {
            val recitation = recitationRepository.getRecitation()
            if (recitation.singleReciter()) {
                val history = History(
                    type = History.LISTENING,
                    chapterId = recitation.chapter.id,
                    chapterName = recitation.chapter.name,
                    reciterId = recitation.reciter.id,
                    reciterName = recitation.reciter.name,
                    verseId = mediaPlayerManager.selectedVerseId,
                    screenMode = if (recitation.screenMode is ScreenMode.Normal) 1 else 2,
                    reelMode = recitation.reelMode,
                    playInBackground = recitation.playInBackground,
                    playLocally = recitation.playLocally,
                    shuffleReciters = recitation.shuffleReciters
                )
                historyRepository.saveHistory(history)
            }
        }
    }

    val tvSlides = slidesRepository.getSlides()

    companion object {
        private const val TAG = "PlaybackViewModel"
    }
}