package com.ma7moud3ly.quran.model

import kotlinx.serialization.Serializable


@Serializable
data class Reciter(
    val id: String,
    val name: String = "",
    val listen: List<Quality> = listOf(),
    val download: List<Quality> = listOf()
) {
    /**
     * Indicates whether the first available listening quality is different from the first available download quality.
     */
    val hasConflictedQualities: Boolean get() = listenQuality != downloadQuality

    /**
     * Indicates whether there are any available qualities for listening.
     */
    val canListen: Boolean get() = listen.isNotEmpty()

    /**
     * The quality level of the first available listening option.
     */
    val listenQuality: Int get() = listen.firstOrNull()?.quality ?: -1

    /**
     * The base URL for the first available listening option.
     */
    val listenBaseUrl: String get() = listen.firstOrNull()?.qualityBaseUrl.orEmpty()

    /**
     * The storage directory path for listened recitations of this reciter at the [listenQuality].
     * Format: "quran/[id]/128"
     */
    val listenStorageDirectory: String get() = "quran/$id/$listenQuality"

    /**
     * Indicates whether there are any available qualities for downloading.
     */
    val canDownload: Boolean get() = download.isNotEmpty()

    /**
     * The quality level of the first available download option.
     */
    val downloadQuality: Int get() = download.firstOrNull()?.quality ?: -1

    /**
     * The ID (often part of the base URL) for the first available download option.
     */
    val downloadBaseUrl: String get() = download.firstOrNull()?.id.orEmpty()

    /**
     * The storage directory path for downloaded recitations of this reciter at the [downloadQuality].
     * Format: "quran/[id]/128"
     */
    val downloadStorageDirectory: String get() = "quran/$id/$downloadQuality"

    /**
     * Retrieves the appropriate quality level based on whether the recitation is local (downloaded) or remote (for listening).
     *
     * @param local If true, returns the [downloadQuality]; otherwise, returns the [listenQuality].
     * @return The quality level.
     */
    fun getQuality(local: Boolean): Int {
        return if (local) downloadQuality else listenQuality
    }
}


@Serializable
data class Quality(
    val quality: Int,
    val id: String
) {
    val hostedOnGithub: Boolean get() = id.startsWith("https://github.com/")
}

/** The base URL for streaming audio of this quality**/
val Quality.qualityBaseUrl: String
    get() =
        if (this.hostedOnGithub) "$id/raw/refs/heads/main/verses"
        else "https://quran.ksu.edu.sa/ayat/mp3/${id}_${quality}kbps"

