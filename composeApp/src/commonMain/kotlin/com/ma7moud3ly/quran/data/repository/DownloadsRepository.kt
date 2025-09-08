package com.ma7moud3ly.quran.data.repository

import com.ma7moud3ly.quran.model.DownloadResult
import com.ma7moud3ly.quran.model.DownloadProgress
import com.ma7moud3ly.quran.model.MediaFile
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for downloading and managing Quran recitations.
 *
 * This repository handles the core logic for downloading chapters and individual verses,
 * tracking download progress, and checking the download status of content.
 */
interface DownloadsRepository {

    val platformSupportDownloading: Boolean
    val downloadProgress: Flow<DownloadProgress>
    val downloadComplete: Flow<DownloadResult>

    suspend fun isFullyDownloaded(path: String, verses: Int): Boolean

    suspend fun getDownloadedChapters(reciterPath: String): List<Int>

    suspend fun downloadChapter(
        downloadId: String,
        url: String,
        outputPath: String
    )

    fun toMediaFile(path: String, link: String = ""): MediaFile

    suspend fun downloadVerse(url: String, path: String): MediaFile
}


