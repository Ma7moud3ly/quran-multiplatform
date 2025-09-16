package com.ma7moud3ly.quran.model

import com.ma7moud3ly.quran.platform.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


data class Recitation(
    val reciters: List<Reciter>,
    val chapter: Chapter,
    val firstVerse: Int,
    val lastVerse: Int,
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

    private val rotationFactor: Int = {
        if (playbackMode.isDistributed) {
            val n = lastVerse + 1 - firstVerse
            val k = reciters.size
            if (n < k) 1
            else {
                val ceil = (n + k - 1) / k       // ceiling division
                val last = n - (k - 1) * ceil    // last slot if we use ceil
                if (last <= 0) n / k else ceil
            }
        } else 1
    }.invoke()

    private var reciterIndex = 0
    val currentReciter: Reciter get() = reciters[reciterIndex]
    private val _reciterFlow = MutableStateFlow<Reciter?>(reciters.first())
    val reciterFlow: Flow<Reciter?> = _reciterFlow.asStateFlow()

    init {
        Log.v(
            "Recitation",
            "reciters: ${reciters.size} ,firstVerse: $firstVerse ,lastVerse: $lastVerse"
        )
        Log.v("Recitation", "rotationFactor - $rotationFactor")
        Log.v("Recitation", "Reciter - ${_reciterFlow.value?.name}")

    }


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
     * Determines if the reciter should be changed based on the current verse number.
     * This is used in distributed playback mode where different reciters read portions of the chapter.
     * The reciter changes when the verse number is a multiple of the [rotationFactor].
     *
     * @param verseInex The current verse number (1-based index).
     * @return True if the reciter should be changed, false otherwise.
     */
    fun canChangeReciter(verseInex: Int) = (verseInex + 1) % rotationFactor == 0

    /**
     * Checks if there is a next reciter in the list.
     * @return True if there is a next reciter, false otherwise.
     */
    fun hasNextReciter(): Boolean = reciterIndex + 1 < reciters.size


    /**
     * Advances to the next reciter in the list if available.
     * Updates [currentReciter] and [_reciterFlow].
     * @return True if successfully moved to the next reciter,
     * false if already at the last reciter.
     */
    fun nextForwardReciter(): Boolean {
        return if (reciterIndex + 1 < reciters.size) {
            reciterIndex++
            _reciterFlow.value = currentReciter
            true
        } else {
            false
        }
    }

    /**
     * Gets the next reciter in the list.
     * If the current reciter is the last one:
     * - If [loop] is true, it returns the first reciter.
     * - If [loop] is false, it returns null.
     *
     * @param loop Whether to loop back to the first reciter if at the end of the list. Defaults to false.
     * @return The next [Reciter] or null if at the end and loop is false.
     */
    fun getNextReciter(loop: Boolean = false): Reciter? {
        return if (reciterIndex + 1 < reciters.size) {
            reciters[reciterIndex + 1]
        } else if (loop) {
            reciters.first()
        } else null
    }

    /**
     * Checks if there is a previous reciter in the list.
     * @return True if there is a reciter before the current one, false otherwise.
     */
    fun hasPreviousReciter(): Boolean = reciterIndex - 1 >= 0

    /**
     * Moves to the previous reciter in the list if available.
     * Updates [currentReciter] and [_reciterFlow].
     * @return True if successfully moved to the previous reciter,
     * false if already at the first reciter.
     */
    fun previousReciter(): Boolean {
        return if (reciterIndex - 1 >= 0) {
            reciterIndex--
            _reciterFlow.value = currentReciter
            true
        } else {
            false
        }
    }

    /**
     * Rotates to the next reciter in the list. If at the end, it wraps around to the first reciter.
     * Updates [currentReciter] and [_reciterFlow].
     */
    fun rotateReciters() {
        if (reciterIndex + 1 < reciters.size) {
            reciterIndex++
            _reciterFlow.value = currentReciter
        } else {
            reciterIndex = 0
            _reciterFlow.value = currentReciter
        }
    }

    /**
     * Rotates to the previous reciter in the list. If at the beginning, it wraps around to the last reciter.
     * Updates [currentReciter] and [_reciterFlow].
     */
    fun rotateBackReciters() {
        return if (reciterIndex - 1 >= 0) {
            reciterIndex--
            _reciterFlow.value = currentReciter
        } else {
            reciterIndex = reciters.size - 1
            _reciterFlow.value = currentReciter
        }
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
