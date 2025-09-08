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
    val shuffleReciters: Boolean = false
) {

    init {
        require(reciters.isNotEmpty()) { println("reciters can't be empty") }
    }

    private var reciterIndex = 0
    /** The currently selected [Reciter]. */
    val reciter: Reciter get() = reciters[reciterIndex]
    /** A [mutableStateOf] holding the current [Reciter], allowing observation of changes. */
    val reciterState = mutableStateOf(reciter)

    /**
     * Checks if the playback is sequential (not shuffled, or only one reciter).
     * @return True if playback is sequential, false otherwise.
     */
    fun isSequentialPlayback(): Boolean {
        return shuffleReciters.not() || reciters.size == 1
    }

    /**
     * Generates a unique identifier string for this recitation session based on its current settings.
     * The ID includes reciter ID, quality, chapter ID, data source (local/remote), and screen mode.
     * Example format: "elafasy-128-1-local-normal"
     * @return A unique string identifier for the recitation.
     */
    fun getUniqueId(): String {
        val reciter = "${reciter.id}-${reciter.getQuality(playLocally)}"
        val chapter = chapter.id
        val source = if (playLocally) "local" else "remote"
        val mode = if (screenMode == ScreenMode.Normal) "normal" else "tv"
        return "$reciter-$chapter-$source-$mode"
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
     * Updates [reciter] and [reciterState].
     * @return True if successfully moved to the next reciter,
     * false if already at the last reciter.
     */
    fun nextReciter(): Boolean {
        return if (reciterIndex + 1 < reciters.size) {
            reciterIndex++
            reciterState.value = reciter
            true
        } else {
            false
        }
    }

    /**
     * Moves to the previous reciter in the list if available.
     * Updates [reciter] and [reciterState].
     * @return True if successfully moved to the previous reciter,
     * false if already at the first reciter.
     */
    fun previousReciter(): Boolean {
        return if (reciterIndex - 1 >= 0) {
            reciterIndex--
            reciterState.value = reciter
            true
        } else {
            false
        }
    }

    /**
     * Rotates to the next reciter in the list. If at the end, it wraps around to the first reciter.
     * Updates [reciter] and [reciterState].
     */
    fun rotateReciters() {
        if (reciterIndex + 1 < reciters.size) {
            reciterIndex++
            reciterState.value = reciter
        } else {
            reciterIndex = 0
            reciterState.value = reciter
        }
    }

    /**
     * Rotates to the previous reciter in the list. If at the beginning, it wraps around to the last reciter.
     * Updates [reciter] and [reciterState].
     */
    fun rotateBackReciters() {
        return if (reciterIndex - 1 >= 0) {
            reciterIndex--
            reciterState.value = reciter
        } else {
            reciterIndex = reciters.size - 1
            reciterState.value = reciter
        }
    }

    /**
     * Resets the current reciter to the first one in the list.
     * Updates [reciter] and [reciterState].
     */
    fun reset() {
        reciterIndex = 0
        reciterState.value = reciter
    }

    /**
     * Indicates whether the current reciter's download quality differs from their listening quality.
     * @see Reciter.hasConflictedQualities
     */
    val hasConflict: Boolean get() = reciter.downloadQuality != reciter.listenQuality

    /**
     * The local directory path for storing downloaded files for the current [reciter] and [chapter].
     * Format: "quran/reciter_id/quality/chapter_id" (e.g., "quran/Houssary/128/1")
     * @see Reciter.downloadStorageDirectory
     * @see Chapter.id
     */
    val downloadDirectory get() = "${reciter.downloadStorageDirectory}/${chapter.id}"

    /**
     * The download link for the ZIP archive of the current [chapter] by the current [reciter].
     * Format: "[Reciter.downloadBaseUrl]/[Chapter.idFormatted()].zip"
     * @see Reciter.downloadBaseUrl
     * @see Chapter.idFormatted
     */
    val downloadLink: String get() = "${reciter.downloadBaseUrl}/${chapter.idFormatted()}.zip"

    /**
     * Generates the local file path for a specific [Verse] of the current [chapter] by the current [reciter].
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
     * The local directory path for storing streamed/listened files for the current [reciter] and [chapter].
     * This is typically used for caching streamed audio.
     * Format: "quran/reciter_id/quality/chapter_id" (e.g., "quran/Houssary/128/114")
     * @see Reciter.listenStorageDirectory
     * @see Chapter.id
     */
    val listenDirectory: String get() = "${reciter.listenStorageDirectory}/${chapter.id}"

    /**
     * Generates a pair of URIs for a specific [Verse]: the remote streaming URL and the potential local cache path.
     *
     * @param verse The [Verse] for which to get the URIs.
     * @return A [Pair] where the first element is the remote streaming URL and the second is the local cache path.
     *         Remote URL example: "https://quran.ksu.edu.sa/ayat/mp3/Hussary_128kbps/001001.mp3"
     *         Local path example: "/quran/Houssary/128/114/001001.mp3"
     * @see Reciter.listenBaseUrl
     * @see Verse.mp3FileName
     */
    fun mediaRemoteUri(verse: Verse): Pair<String, String> {
        val mp3FileName = verse.mp3FileName(chapter.id)
        val remoteLink = "${reciter.listenBaseUrl}/$mp3FileName"
        val localPath = "$listenDirectory/$mp3FileName"
        return Pair(remoteLink, localPath)
    }

}
sealed interface ScreenMode {
    data object Normal : ScreenMode
    data object Tv : ScreenMode
}

data class RecitationState(
    private val canChangeChapter: Boolean = true,
    private val canChangeReciter: Boolean = true,
    private val canChangeVerse: Boolean = true,
    private val firstVerse: Int = 1,
    private val lastVerse: Int = 1,
    private val singleVerse: Boolean = canChangeVerse.not(),
    private val reelMode: Boolean = false,
    private val multiReciters: Boolean = false,
    private val playInBackground: Boolean = false,
    private val shuffleMode: Boolean = false
) {

    val canChangeChapterState = mutableStateOf(canChangeChapter)
    val canChangeReciterState = mutableStateOf(canChangeReciter)
    val canChangeVerseState = mutableStateOf(canChangeVerse)

    val firstVerseState = mutableStateOf(firstVerse)
    val lastVerseState = mutableStateOf(lastVerse)
    val singleVerseState = mutableStateOf(singleVerse)
    val reelModeState = mutableStateOf(reelMode)
    val multiRecitersState = mutableStateOf(multiReciters)
    val shuffleModeState = mutableStateOf(shuffleMode)
    val playInBgState = mutableStateOf(playInBackground)

    fun getReelMode() = reelModeState.value
    fun getPlayInBackground() = playInBgState.value
    fun getMultiReciters() = multiRecitersState.value
    fun getFirstVerse() = firstVerseState.value
    fun getLastVerse(): Int {
        return if (singleVerseState.value) firstVerseState.value
        else lastVerseState.value
    }

    fun getShuffleMode(): Boolean {
        return shuffleModeState.value &&
                singleVerseState.value.not() &&
                multiRecitersState.value
    }

    fun isMultiReciter() = multiRecitersState.value

    fun setMultiReciters(value: Boolean) {
        multiRecitersState.value = value
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