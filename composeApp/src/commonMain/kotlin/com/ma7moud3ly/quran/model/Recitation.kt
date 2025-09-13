package com.ma7moud3ly.quran.model

import androidx.compose.runtime.mutableStateOf


data class Recitation(
    val reciters: List<Reciter>,
    val chapter: Chapter,
    val selectedVerse: Int,
    val lastVerseNumber: Int,
    val screenMode: ScreenMode = ScreenMode.Normal,
    val reelMode: Boolean = false,
    val playInBackground: Boolean = false,
    var playLocally: Boolean = true,
    val loops: Int = 1,
    val playbackMode: PlaybackMode = PlaybackMode.Single
) {

    init {
        require(reciters.isNotEmpty()) { println("reciters can't be empty") }
    }

    private var reciterIndex = 0
    val currentReciter: Reciter get() = reciters[reciterIndex]
    val reciterState = mutableStateOf(currentReciter)

    /**
     * Generates a unique identifier string for this recitation session based on its current settings.
     * The ID includes reciter ID, quality, chapter ID, data source (local/remote), and screen mode.
     * Example format: "elafasy-128-1-local-normal"
     * @return A unique string identifier for the recitation.
     */
    fun getUniqueId(): String {
        val reciter = "${currentReciter.id}-${currentReciter.getQuality(playLocally)}"
        val recitersCount = reciters.size
        val chapter = chapter.id
        val source = if (playLocally) "local" else "remote"
        val mode = if (screenMode == ScreenMode.Normal) "normal" else "tv"
        return "$reciter-$recitersCount-$chapter-$source-$mode"
    }

    fun setOnlineDatasource() {
        playLocally = false
    }

    fun setLocalDatasource() {
        playLocally = true
    }

    /**
     * Checks if there is only one reciter in the list.
     */
    fun singleReciter(): Boolean = reciters.size == 1

    /**
     * Advances to the next reciter in the list if available.
     * Updates [currentReciter] and [reciterState].
     * @return True if successfully moved to the next reciter,
     * false if already at the last reciter.
     */
    fun nextForwardReciter(): Boolean {
        return if (reciterIndex + 1 < reciters.size) {
            reciterIndex++
            reciterState.value = currentReciter
            true
        } else {
            false
        }
    }

    fun geNextReciter(loop: Boolean = false): Reciter? {
        return if (reciterIndex + 1 < reciters.size) {
            reciters[reciterIndex + 1]
        } else if (loop) {
            reciters.first()
        } else null
    }

    /**
     * Moves to the previous reciter in the list if available.
     * Updates [currentReciter] and [reciterState].
     * @return True if successfully moved to the previous reciter,
     * false if already at the first reciter.
     */
    fun previousReciter(): Boolean {
        return if (reciterIndex - 1 >= 0) {
            reciterIndex--
            reciterState.value = currentReciter
            true
        } else {
            false
        }
    }

    /**
     * Rotates to the next reciter in the list. If at the end, it wraps around to the first reciter.
     * Updates [currentReciter] and [reciterState].
     */
    fun rotateReciters() {
        if (reciterIndex + 1 < reciters.size) {
            reciterIndex++
            reciterState.value = currentReciter
        } else {
            reciterIndex = 0
            reciterState.value = currentReciter
        }
    }

    /**
     * Rotates to the previous reciter in the list. If at the beginning, it wraps around to the last reciter.
     * Updates [currentReciter] and [reciterState].
     */
    fun rotateBackReciters() {
        return if (reciterIndex - 1 >= 0) {
            reciterIndex--
            reciterState.value = currentReciter
        } else {
            reciterIndex = reciters.size - 1
            reciterState.value = currentReciter
        }
    }

    /**
     * Resets the current reciter to the first one in the list.
     * Updates [currentReciter] and [reciterState].
     */
    fun reset() {
        reciterIndex = 0
        reciterState.value = currentReciter
    }

    /**
     * The local directory path for storing downloaded files for the current [currentReciter] and [chapter].
     * Format: "quran/reciter_id/quality/chapter_id" (e.g., "quran/Houssary/128/1")
     * @see Reciter.downloadStorageDirectory
     * @see Chapter.id
     */
    val downloadDirectory get() = "${currentReciter.downloadStorageDirectory}/${chapter.id}"

    /**
     * The download link for the ZIP archive of the current [chapter] by the current [currentReciter].
     * Format: "[Reciter.downloadBaseUrl]/[Chapter.idFormatted()].zip"
     * @see Reciter.downloadBaseUrl
     * @see Chapter.idFormatted
     */
    val downloadLink: String get() = "${currentReciter.downloadBaseUrl}/${chapter.idFormatted()}.zip"

    /**
     * Generates the local file path for a specific [Verse] of the current [chapter] by the current [currentReciter].
     * Assumes the file is downloaded.
     * Format: "[downloadDirectory]/[Verse.mp3FileName(chapter.id)]"
     * (e.g., "/quran/Houssary/128/1/001001.mp3")
     *
     * @param verse The [Verse] for which to get the local URI.
     * @return The local file path string.
     * @see Verse.mp3FileName
     */
    fun medialLocalUri(verse: Verse): String {
        val mp3FileName = verse.mp3FileName(chapter.id)
        return "$downloadDirectory/$mp3FileName"
    }

    /**
     * The local directory path for storing streamed/listened files for the current [currentReciter] and [chapter].
     * This is typically used for caching streamed audio.
     * Format: "quran/reciter_id/quality/chapter_id" (e.g., "quran/Houssary/128/114")
     * @see Reciter.listenStorageDirectory
     * @see Chapter.id
     */
    fun remoteStorageDirectory(reciter: Reciter = currentReciter): String {
        return "${reciter.listenStorageDirectory}/${chapter.id}"
    }

    /**
     * Generates a pair of URIs for a specific [Verse]: the remote streaming URL and the potential local cache path.
     * The URIs are generated based on the provided [Reciter] or the [currentReciter] if none is specified.
     *
     * @param reciter The [Reciter] for which to generate the URIs. Defaults to [currentReciter].
     * @param verse The [Verse] for which to get the URIs.
     * @return A [Pair] where the first element is the remote streaming URL and the second is the local cache path.
     *         Remote URL example: "https://quran.ksu.edu.sa/ayat/mp3/Hussary_128kbps/001001.mp3"
     *         Local path example: "/quran/Houssary/128/114/001001.mp3"
     * @see Reciter.listenBaseUrl
     * @see Verse.mp3FileName
     * @see remoteStorageDirectory
     */
    fun mediaRemoteUri(
        reciter: Reciter = currentReciter,
        verse: Verse
    ): Pair<String, String> {
        val mp3FileName = verse.mp3FileName(chapter.id)
        val remoteLink = "${reciter.listenBaseUrl}/$mp3FileName"
        val localPath = "${remoteStorageDirectory(reciter)}/$mp3FileName"
        return Pair(remoteLink, localPath)
    }

}

sealed interface ScreenMode {
    data object Normal : ScreenMode
    data object Tv : ScreenMode
}

sealed interface PlaybackMode {
    data object Single : PlaybackMode
    data object Multiple : PlaybackMode
    data object Shuffling : PlaybackMode
}

val PlaybackMode.isSingleReciter get() = this is PlaybackMode.Single
val PlaybackMode.isMultipleReciters get() = this is PlaybackMode.Multiple
val PlaybackMode.isShufflingReciters get() = this is PlaybackMode.Shuffling
val PlaybackMode.toInt
    get() = when (this) {
        is PlaybackMode.Single -> 0
        is PlaybackMode.Multiple -> 1
        is PlaybackMode.Shuffling -> 2
    }
val Int.toPlaybackMode
    get() = when (this) {
        0 -> PlaybackMode.Single
        1 -> PlaybackMode.Multiple
        2 -> PlaybackMode.Shuffling
        else -> PlaybackMode.Single
    }


data class RecitationState(
    private val canChangeChapter: Boolean = true,
    private val canChangeReciter: Boolean = true,
    private val canChangeVerse: Boolean = true,
    private val firstVerse: Int = 1,
    private val lastVerse: Int = 1,
    private val singleVerse: Boolean = canChangeVerse.not(),
    private val reelMode: Boolean = false,
    private val playInBackground: Boolean = false,
    private val playbackMode: PlaybackMode = PlaybackMode.Single
) {

    val canChangeChapterState = mutableStateOf(canChangeChapter)
    val canChangeReciterState = mutableStateOf(canChangeReciter)
    val canChangeVerseState = mutableStateOf(canChangeVerse)

    val firstVerseState = mutableStateOf(firstVerse)
    val lastVerseState = mutableStateOf(lastVerse)
    val singleVerseState = mutableStateOf(singleVerse)
    val reelModeState = mutableStateOf(reelMode)
    val playbackModeState = mutableStateOf(playbackMode)
    val playInBgState = mutableStateOf(playInBackground)

    fun getReelMode() = reelModeState.value
    fun getPlayInBackground() = playInBgState.value
    fun getFirstVerse() = firstVerseState.value
    fun getLastVerse(): Int {
        return if (singleVerseState.value) firstVerseState.value
        else lastVerseState.value
    }

    fun getPlaybackMode() = playbackModeState.value

    fun isSingleReciterMode() = playbackModeState.value.isSingleReciter

    fun setPlaybackMode(value: PlaybackMode) {
        playbackModeState.value = value
    }

    fun setFirstVerse(value: Int) {
        firstVerseState.value = value
    }

    fun setLastVerse(value: Int) {
        lastVerseState.value = value
    }

    fun setSingleVerse(value: Boolean) {
        singleVerseState.value = value
    }

    fun setContentLock(
        canChangeChapter: Boolean,
        canChangeReciter: Boolean,
        canChangeVerse: Boolean
    ) {
        canChangeChapterState.value = canChangeChapter
        canChangeReciterState.value = canChangeReciter
        canChangeVerseState.value = canChangeVerse
    }


}