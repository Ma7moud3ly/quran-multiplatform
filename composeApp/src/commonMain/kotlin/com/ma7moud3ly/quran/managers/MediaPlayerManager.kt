package com.ma7moud3ly.quran.managers

import androidx.compose.runtime.mutableStateOf
import com.ma7moud3ly.quran.data.repository.DownloadsRepository
import com.ma7moud3ly.quran.data.repository.RecitationRepository
import com.ma7moud3ly.quran.model.MediaFile
import com.ma7moud3ly.quran.model.ScreenMode
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.platform.AudioFocusEvents
import com.ma7moud3ly.quran.platform.AudioFocus
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.MediaPlayer
import com.ma7moud3ly.quran.platform.Platform
import com.ma7moud3ly.quran.platform.isAndroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single


/**
 * Manages media playback for Quran recitations.
 *
 * This class handles the initialization, control, and lifecycle of media playback,
 * including fetching recitation data, managing verses, handling audio focus,
 * and interacting with the platform's media player.
 *
 * @property recitationRepository Repository for accessing recitation data.
 * @property downloadsRepository Repository for managing downloaded media files (optional).
 * @property platform Provides platform-specific functionalities.
 */
@Single
class MediaPlayerManager(
    private val recitationRepository: RecitationRepository,
    private val downloadsRepository: DownloadsRepository,
    private val platform: Platform
) {
    /** Coroutine scope for managing background tasks. */
    private val playerCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /** Job for the current verse playback. */
    private var versePlaybackJob: Job? = null

    /** The underlying platform media player instance. */
    private val mediaPlayer = MediaPlayer()

    /** Manages audio focus requests and events. */
    private val audioFocus = AudioFocus()

    /** The current recitation being played. */
    private lateinit var recitation: Recitation

    /** Manages the sequence of verses for the current recitation. */
    private lateinit var versesManager: VersesManager

    fun getChapter() = recitation.chapter
    fun getReciter() = reciterState.value
    fun getVerseManager() = versesManager
    val isNormalScreenMode get() = recitation.screenMode == ScreenMode.Normal
    val isReelMode get() = recitation.reelMode
    val playInBackground get() = recitation.playInBackground
    val chapterName get() = recitation.chapter.chapterFullName()
    val reciterState get() = recitation.reciterState
    val currentVerse get() = versesManager.selectedVerse
    val selectedVerseId get() = versesManager.selectedVerseId
    val loops get() = recitation.loops
    var hasReleased: Boolean = true

    /** Mutable state indicating if a verse is currently being downloaded. */
    val isDownloadingVerse = mutableStateOf(false)

    /** Mutable state indicating if (recitation) is currently playing. */
    val isPlaying = mutableStateOf(false)

    /** Shared flow to signal when playback has finished. */
    private val _finishPlayback = MutableSharedFlow<Boolean>()
    val finishPlayback: SharedFlow<Boolean> = _finishPlayback.asSharedFlow()

    /**
     * Initializes playback for the current recitation.
     *
     * This method performs the following actions:
     * 1. Retrieves the current recitation from the [recitationRepository].
     * 2. If the media player is already playing the same recitation, it resumes playback.
     * 3. Otherwise, it initializes the [recitation] and [versesManager].
     * 4. Sets up background playback if enabled and supported by the platform.
     * 5. Requests audio focus.
     * 6. Collects audio focus events to pause or resume playback accordingly.
     * 7. Collects changes to the [currentVerse] and initiates playback for the new verse.
     *    - It handles looping of the verse.
     *    - It automatically moves to the next verse after the current one finishes (if applicable).
     */
    @OptIn(FlowPreview::class)
    fun initPlayBack() {
        val newRecitation = recitationRepository.getRecitation()
        if (mediaPlayer.isPlaying() && recitation.getUniqueId() == newRecitation.getUniqueId()) {
            Log.i(TAG, "ResumePlayBack")
            return
        }
        Log.i(TAG, "initPlayBack")
        recitation = newRecitation
        hasReleased = false
        versesManager = VersesManager(
            verses = recitation.chapter.verses,
            initialVerseId = recitation.selectedVerse
        )
        if (platform.isAndroid) {
            if (playInBackground) {
                mediaPlayer.playInBackground()
            } else {
                mediaPlayer.hideBackgroundNotification()
            }
        }

        audioFocus.requestAudioFocus()
        playerCoroutineScope.launch {
            audioFocus.audioFocusFlow.collect { event ->
                when (event) {
                    AudioFocusEvents.PLAY -> {
                        Log.v(TAG, "AudioFocusEvents - PLAY")
                        resume()
                    }

                    AudioFocusEvents.PAUSE -> {
                        Log.v(TAG, "AudioFocusEvents - PAUSE")
                        pause()
                    }

                    AudioFocusEvents.STOP -> {
                        Log.v(TAG, "AudioFocusEvents - STOP")
                        pause()
                    }

                    else -> {}
                }
            }
        }

        playerCoroutineScope.launch {
            currentVerse.debounce(200).collect { verse ->
                if (verse == null) {
                    Log.v(TAG, "verse:null")
                    return@collect
                }
                Log.v(TAG, "${reciterState.value.name} verse: ${verse.id}")
                versePlaybackJob?.cancel()
                versePlaybackJob = launch {
                    for (i in 1..loops) {
                        val success = play(verse)
                        Log.v(TAG, "play success - $success")
                        if (hasReleased || success.not()) {
                            return@launch
                        }
                        if (i == loops) {
                            next(finishOnLastVerse = true)
                        } else {
                            Log.v(TAG, "loop #${i + 1}")
                            resume()
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if a (recitation) is currently playing.
     *
     * @return `true` if playing, `false` otherwise.
     */
    fun isPlaying(): Boolean {
        return isPlaying.value
    }

    /**
     * Retrieves the [MediaFile] for the given [Verse].
     *
     * This function determines the remote URI and local path for the verse's media
     * based on the current [recitation] and then uses the [downloadsRepository]
     * to create a [MediaFile] object.
     *
     * @return The [MediaFile] corresponding to this verse.
     */
    private fun Verse.getMediaFile(): MediaFile {
        val (link, path) = recitation.mediaRemoteUri(this)
        val mediaFile = downloadsRepository.toMediaFile(path, link)
        return mediaFile
    }

    /**
     * Proactively downloads the next verse if it's not already cached.
     * This helps ensure smoother playback transitions.
     * It checks for the next verse, its media file, and downloads it if missing.
     */
    private suspend fun downloadNextVerse(): Boolean {
        // when there is not next verse, return true
        val verse = versesManager.getNextVerse() ?: return true
        val mediaFile = verse.getMediaFile()
        // verse has previously downloaded
        if (mediaFile.exists) return true
        Log.i(TAG, "-----> Started downloading next verse ${verse.id}")
        val file = downloadsRepository.downloadVerse(mediaFile)
        Log.i(TAG, "-----< Finished downloading verse ${verse.id} - success: ${file.exists}")
        return file.exists
    }

    /**
     * Downloads the current verse if not locally available.
     * Updates [isDownloadingVerse] state during the download.
     *
     * @param mediaFile The verse to download.
     * @return The [MediaFile] with the local path if successful.
     */
    private suspend fun downloadCurrentVerse(mediaFile: MediaFile): MediaFile {
        isDownloadingVerse.value = true
        val file = downloadsRepository.downloadVerse(mediaFile)
        isDownloadingVerse.value = false
        return file
    }

    /**
     * Prepares a verse for playback.
     *
     * This is a suspend function that uses a cancellable coroutine.
     * It sets up the media player for the given [mediaFile] and starts playback.
     *
     * @param mediaFile The media file to prepare and play.
     */
    private suspend fun prepareVerse(mediaFile: MediaFile) {
        return suspendCancellableCoroutine { continuation ->
            mediaPlayer.prepare(mediaFile) {
                isPlaying.value = true
                mediaPlayer.start {
                    Log.v(TAG, "finish playing..")
                    if (continuation.isActive) {
                        continuation.resume(Unit) { cause, _, _ -> }
                    }
                }
            }
            continuation.invokeOnCancellation {
                Log.v(TAG, "prepareVerse cancelled")
            }
        }
    }

    /**
     * Plays a verse from a remote source (online).
     *
     * It handles downloading the verse if it's not already available locally
     * and if the platform supports downloading.
     *
     * @param verse The verse to play.
     */
    private suspend fun playRemoteVerse(verse: Verse): Boolean {
        val mediaFile = verse.getMediaFile()
        Log.v(TAG, "playOnlineVerse: $mediaFile")
        if (downloadsRepository.platformSupportDownloading) {
            if (mediaFile.exists) {
                return coroutineScope {
                    val prepared = async { prepareVerse(mediaFile) }
                    val downloaded = async { downloadNextVerse() }
                    prepared.await()
                    downloaded.await()
                }
            } else {
                val downloadedFile = downloadCurrentVerse(mediaFile)
                if (downloadedFile.exists.not()) return false
                return coroutineScope {
                    val prepared = async { prepareVerse(downloadedFile) }
                    val downloaded = async { downloadNextVerse() }
                    prepared.await()
                    downloaded.await()
                }
            }
        } else {
            prepareVerse(mediaFile)
        }
        return true
    }

    /**
     * Plays a verse from a local source.
     * It checks if the media file exists before attempting to play it.
     *
     * @param verse The verse to play.
     */
    private suspend fun playLocalVerse(verse: Verse): Boolean {
        val versePath = recitation.medialLocalUri(verse)
        val mediaFile = downloadsRepository.toMediaFile(versePath)
        Log.v(TAG, "playLocalVerse: $mediaFile")
        if (mediaFile.exists) prepareVerse(mediaFile)
        return mediaFile.exists
    }

    /**
     * Plays the specified verse.
     *
     * It determines whether to play the verse locally or remotely based on the
     * [recitation]'s settings.
     *
     * @param verse The verse to play.
     */
    suspend fun play(verse: Verse): Boolean {
        Log.i(TAG, "play ${reciterState.value.name} - ${verse.id}")
        return if (recitation.playLocally) playLocalVerse(verse)
        else playRemoteVerse(verse)
    }

    /**
     * Moves to the next verse or reciter based on the playback mode.
     * Pauses playback before attempting to move.
     *
     * @return `true` if successfully moved to the next verse/reciter,
     * `false` otherwise (e.g., end of recitation).
     */
    private suspend fun nextVerseOrReciter(): Boolean {
        pause()
        Log.v(TAG, "nextVerseOrReciter")
        val nextVerse = versesManager.nextForwardVerse()
        if (nextVerse.not()) {
            val nextReciter = recitation.nextReciter()
            if (nextReciter) versesManager.reset()
            else return false
        }
        return true
    }

    /**
     * Moves to the previous verse or reciter based on the playback mode.
     * Pauses playback before attempting to move.
     *
     * @return `true` if successfully moved to the previous verse/reciter,
     * `false` otherwise (e.g., beginning of recitation).
     */
    private suspend fun previousVerseOrReciter(): Boolean {
        pause()
        Log.v(TAG, "previousVerseOrReciter")
        val previousVerse = versesManager.previousVerseInRange()
        if (previousVerse.not()) {
            val previousReciter = recitation.previousReciter()
            if (previousReciter) versesManager.reset()
            else return false
        }
        return true
    }

    /**
     * Moves to the next verse and rotates to the next reciter.
     * This is used when playback is not sequential (e.g., rotating reciters for each verse).
     * Pauses playback before attempting to move.
     *
     * @return `true` if successfully moved to the next verse,
     * `false` if there is no next verse.
     */
    private fun nextVerseAndReciter(): Boolean {
        if (versesManager.hasNext().not()) return false
        pause()
        Log.v(TAG, "nextVerseAndReciter")
        recitation.rotateReciters()
        val nextVerse = versesManager.nextForwardVerse()
        return nextVerse
    }

    /**
     * Moves to the previous verse and rotates back to the previous reciter.
     * This is used when playback is not sequential (e.g., rotating reciters for each verse).
     * Pauses playback before attempting to move.
     *
     * @return `true` if successfully moved to the previous verse,
     * `false` if there is no previous verse.
     */
    private fun previousVerseAndReciter(): Boolean {
        if (versesManager.hasPrevious().not()) return false
        pause()
        Log.v(TAG, "previousVerseAndReciter")
        recitation.rotateBackReciters()
        val previousVerse = versesManager.previousVerseInRange()
        return previousVerse
    }

    /**
     * Moves to the next verse or reciter.
     *
     * If [finishOnLastVerse] is `true` and there are no more verses/reciters,
     * it triggers the finish callback and releases resources.
     * @param finishOnLastVerse Whether to finish playback if this is the last verse.
     */
    fun next(finishOnLastVerse: Boolean = false) {
        playerCoroutineScope.launch {
            val hasNext = if (recitation.isSequentialPlayback()) {
                nextVerseOrReciter()
            } else {
                nextVerseAndReciter()
            }
            if (hasNext.not() && finishOnLastVerse) {
                onFinishCallback()
                release()
            }
        }
    }

    /**
     * Moves to the previous verse or reciter.
     */
    fun previous(finishOnLastVerse: Boolean = false) {
        playerCoroutineScope.launch {
            val hasPrevious = if (recitation.isSequentialPlayback()) {
                previousVerseOrReciter()
            } else {
                previousVerseAndReciter()
            }
            if (hasPrevious.not() && finishOnLastVerse) {
                onFinishCallback()
                release()
            }
        }
    }

    /**
     * Resumes playback.
     * If resources have been released, it re-initializes playback.
     * Otherwise, it starts the media player and requests audio focus.
     */
    fun resume() {
        if (hasReleased.not()) {
            mediaPlayer.start()
            isPlaying.value = true
            audioFocus.requestAudioFocus()
        } else {
            initPlayBack()
        }
    }

    /**
     * Pauses playback.
     * It pauses the media player, releases audio focus temporarily,
     * and updates the playing state.
     */
    fun pause() {
        mediaPlayer.pause()
        audioFocus.pause()
        isPlaying.value = false
    }

    /**
     * Callback function invoked when playback finishes.
     * Emits `true` to the [finishPlayback] shared flow.
     */
    private fun onFinishCallback() {
        playerCoroutineScope.launch {
            _finishPlayback.emit(true)
        }
    }

    /**
     * Releases all resources associated with media playback.
     * This includes resetting the recitation, releasing the media player,
     * audio focus, and background service (if applicable).
     */
    fun release() {
        Log.i(TAG, "release")
        recitation.reset()
        mediaPlayer.release()
        hasReleased = true
        isPlaying.value = false
        audioFocus.release()
        if (playInBackground) {
            mediaPlayer.releaseBackgroundService()
        }
    }

    /**
     * Releases resources when triggered from a background context (e.g., notification).
     * If resources are already released, it does nothing.
     * Otherwise, it releases resources and triggers the finish callback.
     */
    fun releaseFromBackground() {
        if (hasReleased) return
        release()
        onFinishCallback()
    }

    companion object {
        private const val TAG = "MediaPlayerManager"
    }
}

