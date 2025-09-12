package com.ma7moud3ly.quran.managers

import androidx.compose.runtime.mutableStateOf
import com.ma7moud3ly.quran.data.repository.DownloadsRepository
import com.ma7moud3ly.quran.data.repository.RecitationRepository
import com.ma7moud3ly.quran.model.MediaFile
import com.ma7moud3ly.quran.model.PlaybackMode
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.ScreenMode
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.platform.AudioFocus
import com.ma7moud3ly.quran.platform.AudioFocusEvents
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
        recitation = newRecitation
        Log.i(TAG, "initPlayBack - ${recitation.playbackMode}")
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
            currentVerse.collect { verse ->
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
    private fun Verse.getMediaFile(reciter: Reciter): MediaFile {
        val (link, path) = recitation.mediaRemoteUri(reciter, verse = this)
        val mediaFile = downloadsRepository.toMediaFile(path, link)
        return mediaFile
    }

    /**
     * Proactively downloads the next verse if it's not already cached.
     * This helps ensure smoother playback transitions.
     * It determines the next verse and reciter based on the current [PlaybackMode].
     * If the next verse's media file is not found locally, it attempts to download it.
     *
     * @return `true` if the next verse is already cached or successfully downloaded,
     *         or if there is no next verse/reciter. Returns `false` if the download fails.
     */
    private suspend fun downloadNextVerse(): Boolean {
        val nextVerse: Verse?
        val reciter: Reciter?
        when (recitation.playbackMode) {
            PlaybackMode.Single -> {
                nextVerse = versesManager.getNextVerse()
                reciter = recitation.currentReciter
            }

            PlaybackMode.Multiple -> {
                if (versesManager.hasNext()) {
                    nextVerse = versesManager.getNextVerse()
                    reciter = recitation.currentReciter
                } else {
                    nextVerse = versesManager.initialVerse
                    reciter = recitation.geNextReciter(loop = false)
                }
            }

            PlaybackMode.Shuffling -> {
                nextVerse = versesManager.getNextVerse()
                reciter = recitation.geNextReciter(loop = true)
            }
        }

        if (nextVerse == null || reciter == null) return true
        val mediaFile = nextVerse.getMediaFile(reciter)
        // verse has previously downloaded
        if (mediaFile.exists) return true
        Log.i(TAG, "-----> Started downloading next verse ${nextVerse.id} - ${reciter.id}")
        val file = downloadsRepository.downloadVerse(mediaFile)
        Log.i(TAG, "-----< Finished downloading verse ${nextVerse.id} - success: ${file.exists}")
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
        val mediaFile = verse.getMediaFile(recitation.currentReciter)
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
     * Moves to the next verse or reciter.
     *
     * If [finishOnLastVerse] is `true` and there are no more verses/reciters,
     * it triggers the finish callback and releases resources.
     * @param finishOnLastVerse Whether to finish playback if this is the last verse.
     */
    fun next(finishOnLastVerse: Boolean = false) {
        playerCoroutineScope.launch {
            pause()
            val hasMore = when (recitation.playbackMode) {
                PlaybackMode.Single -> {
                    val nextVerse = versesManager.nextForwardVerse()
                    nextVerse
                }

                PlaybackMode.Multiple -> {
                    val nextVerse = versesManager.nextForwardVerse()
                    if (nextVerse) {
                        true
                    } else {
                        val hasNextReciter = recitation.nextForwardReciter()
                        if (hasNextReciter) versesManager.reset()
                        hasNextReciter
                    }
                }

                PlaybackMode.Shuffling -> {
                    recitation.rotateReciters()
                    val nextVerse = versesManager.nextForwardVerse()
                    nextVerse
                }
            }

            if (hasMore.not() && finishOnLastVerse) {
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
            pause()
            val hasPrevious = when (recitation.playbackMode) {
                PlaybackMode.Single -> {
                    val previousVerse = versesManager.previousVerseInRange()
                    Log.v(TAG, "previousVerse $previousVerse")
                    previousVerse
                }

                PlaybackMode.Multiple -> {
                    val previousVerse = versesManager.previousVerseInRange()
                    if (previousVerse) {
                        Log.v(TAG, "previousVerse $previousVerse")
                        true
                    } else {
                        val previousReciter = recitation.previousReciter()
                        if (previousReciter) versesManager.reset()
                        Log.v(TAG, "previousReciter $previousReciter")
                        previousReciter
                    }
                }

                PlaybackMode.Shuffling -> {
                    if (versesManager.hasPrevious()) {
                        recitation.rotateBackReciters()
                        val previousVerse = versesManager.previousVerseInRange()
                        Log.v(TAG, "previousVerse $previousVerse")
                        previousVerse
                    } else false
                }
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

