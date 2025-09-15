package com.ma7moud3ly.quran.features.recitation.config

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma7moud3ly.quran.AppRoutes
import com.ma7moud3ly.quran.data.repository.ChaptersRepository
import com.ma7moud3ly.quran.data.repository.DownloadsRepository
import com.ma7moud3ly.quran.data.repository.RecitationRepository
import com.ma7moud3ly.quran.data.repository.RecitersRepository
import com.ma7moud3ly.quran.data.repository.SettingsRepository
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.History
import com.ma7moud3ly.quran.model.PlaybackMode
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.isSingleReciter
import com.ma7moud3ly.quran.model.toPlaybackMode
import com.ma7moud3ly.quran.platform.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

private const val TAG = "RecitationViewModel"

@KoinViewModel
class RecitationViewModel(
    private val chaptersRepository: ChaptersRepository,
    private val recitersRepository: RecitersRepository,
    private val downloadsRepository: DownloadsRepository,
    private val recitationRepository: RecitationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val recitationState = recitationRepository.recitationState

    val recitationFlow: StateFlow<Recitation?> = recitationRepository.recitationFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = null
    )

    fun setRecitation(recitation: Recitation) {
        recitationRepository.setRecitation(recitation)
    }

    fun setOnlineDataSource() {
        recitationRepository.setOnlineDataSource()
    }

    fun setRecitation(history: History) {
        viewModelScope.launch {
            val chapter = chaptersRepository.getChapter(history.chapterId) ?: return@launch
            val reciter = recitersRepository.getReciter(history.reciterId!!) ?: return@launch
            val recitation = Recitation(
                reciters = listOf(reciter),
                chapter = chapter,
                firstVerse = history.verseId,
                lastVerse = chapter.verses.last().id,
                screenMode = history.screenModeName,
                reelMode = history.reelMode,
                playInBackground = history.playInBackground,
                playLocally = history.playLocally,
                playbackMode = history.playbackMode.toPlaybackMode,
            )
            Log.v(TAG, " $recitation")
            recitationRepository.setRecitation(recitation)
        }
    }

    private var configTime: Long = 0
    fun initConfig(config: AppRoutes.Recitation.Config) {
        if (configTime == config.timestamp) return
        configTime = config.timestamp

        if (config.chapterId != null) {
            getChapter(config.chapterId, config.verseId)
        }

        if (config.reciterId != null) {
            addReciter(config.reciterId, clear = true)
        } else if (config.canChangeChapter) {
            getLastReciters()
        }
    }

    /**
     * Chapter
     */

    private val _chapterFlow = MutableStateFlow<Chapter?>(null)
    val chapterFlow: StateFlow<Chapter?> = _chapterFlow.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = null
    )


    fun getChapter(id: Int, verseId: Int? = null) {
        val state = recitationState.value
        if (_chapterFlow.value?.id == id) {
            state.setFirstVerse(verseId ?: 1)
            return
        }
        viewModelScope.launch {
            chaptersRepository.getChapter(id)?.let {
                _chapterFlow.value = it
                state.setFirstVerse(verseId ?: 1)
                state.setLastVerse(it.verses.last().id)
                state.setSingleVerse(false)
            }
        }
    }

    /**
     * Reciter
     */


    val reciters = mutableStateListOf<Reciter>()

    fun addReciter(id: String, clear: Boolean = false) {
        val state = recitationState.value
        if (clear) {
            reciters.clear()
            state.setPlaybackMode(PlaybackMode.Single)
        }
        viewModelScope.launch {
            val reciter = recitersRepository.getReciter(id) ?: return@launch
            val single = state.getPlaybackMode().isSingleReciter
            if (single) reciters.clear()
            if (reciters.contains(reciter).not()) {
                if (single) reciters.add(reciter)
                else if (reciter.canListen) reciters.add(reciter)
            }
            reciters.firstOrNull()?.let { getDownloadedChapters(it) }
        }
    }

    fun removeReciter(reciter: Reciter) {
        reciters.remove(reciter)
        if (reciters.size <= 1) recitationState.value.setPlaybackMode(PlaybackMode.Single)
    }

    fun saveLastReciters() {
        if (reciters.isNotEmpty()) {
            settingsRepository.saveLastReciters(reciters)
        }
    }

    private fun getLastReciters() {
        viewModelScope.launch {
            val reciterIds = settingsRepository.getLastReciterIds()
            if (reciterIds.isEmpty()) return@launch
            val lastReciters = recitersRepository.getReciters(reciterIds).toMutableList()
            if (lastReciters.isEmpty()) {
                val firstReciter = recitersRepository.getRecitersIndex().firstOrNull()
                if (firstReciter != null) lastReciters.add(firstReciter)
            }
            reciters.apply {
                clear()
                addAll(lastReciters)
            }
            val playbackMode = if (lastReciters.size > 1) PlaybackMode.Repetitive
            else PlaybackMode.Single
            recitationState.value.setPlaybackMode(playbackMode)
            reciters.firstOrNull()?.let { getDownloadedChapters(it) }
        }
    }

    /**
     * Downloaded chapters
     */

    val downloadedChapters = mutableStateListOf<Chapter>()

    fun updateDownloads() {
        viewModelScope.launch {
            reciters.firstOrNull()?.let {
                getDownloadedChapters(it)
            }
        }
    }

    private suspend fun getDownloadedChapters(reciter: Reciter) {
        if (downloadsRepository.platformSupportDownloading) {
            val ids1 = downloadsRepository.getDownloadedChapters(
                reciterPath = reciter.downloadStorageDirectory
            )
            val ids2 = if (reciter.hasConflictedQualities) {
                downloadsRepository.getDownloadedChapters(
                    reciterPath = reciter.listenStorageDirectory
                )
            } else emptyList()
            val ids = (ids1 + ids2).toSet()
            val chapters = chaptersRepository.getChapters(ids)
            downloadedChapters.clear()
            downloadedChapters.addAll(chapters)
        }
    }

    suspend fun isFullyDownloaded(recitation: Recitation): Boolean {
        return downloadsRepository.isFullyDownloaded(
            path = recitation.downloadDirectory,
            verses = recitation.chapter.verses.size
        )
    }

    suspend fun isFullyCachedCached(recitation: Recitation): Boolean {
        return downloadsRepository.isFullyDownloaded(
            path = recitation.remoteStorageDirectory(),
            verses = recitation.chapter.verses.size
        )
    }

    fun platformSupportDownloading(): Boolean {
        return downloadsRepository.platformSupportDownloading
    }
}