package com.ma7moud3ly.quran.data.impl

import com.ma7moud3ly.quran.data.repository.DownloadsRepository
import com.ma7moud3ly.quran.managers.DownloadManager
import com.ma7moud3ly.quran.model.DownloadResult
import com.ma7moud3ly.quran.model.DownloadProgress
import com.ma7moud3ly.quran.model.MediaFile
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.getAppLocalDataStoragePath
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class DownloadsRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val downloadManager: DownloadManager,
    private val platformLocalDataDirectory: String = getAppLocalDataStoragePath()
) : DownloadsRepository {

    companion object {
        private const val TAG = "DownloadsRepository"
    }

    override val platformSupportDownloading: Boolean = downloadManager.platformSupportDownloading()

    private fun platformFullPath(path: String): String {
        return "$platformLocalDataDirectory/$path"
    }

    private val _downloadsProgress = MutableSharedFlow<DownloadProgress>(replay = 1)
    override val downloadProgress: Flow<DownloadProgress> = _downloadsProgress.asSharedFlow()

    private val _downloadComplete = MutableSharedFlow<DownloadResult>(replay = 1)
    override val downloadComplete: Flow<DownloadResult> = _downloadComplete.asSharedFlow()

    override suspend fun isFullyDownloaded(
        path: String,
        verses: Int
    ): Boolean = withContext(dispatcher) {
        downloadManager.isFullyDownloaded(
            path = platformFullPath(path),
            verses = verses
        )
    }

    override suspend fun getDownloadedChapters(
        reciterPath: String
    ): List<Int> = withContext(dispatcher) {
        downloadManager.getDownloadedChapters(
            reciterPath = platformFullPath(reciterPath)
        )
    }

    override suspend fun downloadChapter(
        downloadId: String,
        url: String,
        outputPath: String
    ) {
        withContext(dispatcher) {
            val fullPath = platformFullPath(outputPath)
            val downloaded = downloadManager.downloadFile(
                url = url,
                path = "$fullPath.zip",
                onProgress = { downloaded, totalSize, percent ->
                    _downloadsProgress.tryEmit(
                        DownloadProgress(
                            downloaded = downloaded,
                            size = totalSize,
                            percent = percent
                        )
                    )
                }
            )
            val downloadResult = if (downloaded) {
                val extracted = downloadManager.unzipMediaFile(fullPath)
                if (extracted) {
                    downloadManager.markChapterAsDownloaded(fullPath)
                    DownloadResult(downloadId, true)
                } else {
                    DownloadResult(downloadId, false)
                }
            } else {
                DownloadResult(downloadId, false)
            }
            _downloadComplete.emit(downloadResult)
            withContext(NonCancellable) {
                // reset download progress
                delay(3000)
                _downloadsProgress.emit(DownloadProgress())
            }
        }
    }


    override fun toMediaFile(path: String, link: String): MediaFile {
        val fullPath = platformFullPath(path)
        val exists = downloadManager.isFileExists(fullPath)
        return MediaFile(path = fullPath, exists = exists, url = link)
    }

    override suspend fun downloadVerse(url: String, path: String): MediaFile =
        withContext(dispatcher) {
            Log.v(TAG,"downloadVerse $path - $url")
            val downloaded = downloadManager.downloadFile(url = url, path = path)
            MediaFile(
                path = path,
                url = url,
                exists = downloaded
            )
        }

}