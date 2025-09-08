package com.ma7moud3ly.quran.managers

import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.Platform
import com.ma7moud3ly.quran.platform.isAndroid
import com.ma7moud3ly.quran.platform.isJvm
import com.ma7moud3ly.quran.platform.unzipFile
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.math.round

/**
 * Manages the downloading, extraction, and tracking of Quran chapters.
 * @property httpClient The Ktor HTTP client for making network requests across platforms.
 * @property fileSystem The Okio FileSystem for interacting with the file system.
 * @property platform The platform-specific utilities.
 */
class DownloadManager(
    private val httpClient: HttpClient,
    private val fileSystem: FileSystem,
    private val platform: Platform
) {

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1024 * 64
        private const val DOT_DOWNLOADED = ".downloaded"
        private const val TAG = "DownloadManager"
        internal val Long.toMb: Double get() = (round((this / 1024.0 / 1024.0) * 100) / 100)
    }

    /**
     * Checks if the current platform supports downloading.
     * @return True if downloading is supported, false otherwise.
     */
    fun platformSupportDownloading() = platform.isAndroid || platform.isJvm

    /**
     * Checks if a file or directory exists at the given path.
     * @param path The absolute path to the file or directory.
     * @return True if the file or directory exists, false otherwise.
     */
    fun isFileExists(path: String): Boolean {
        return fileSystem.exists(path.toPath())
    }

    /**
     * Checks if a chapter has been fully downloaded and extracted.
     * @param path The absolute path to the chapter's directory.
     * @param verses The total number of verses in the chapter.
     * @return True if the chapter is fully downloaded, false otherwise.
     */
    suspend fun isFullyDownloaded(path: String, verses: Int): Boolean {
        val path = path.toPath()
        val dotDownloadedFile = "$path/$DOT_DOWNLOADED".toPath()

        // not downloaded yet
        return if (!fileSystem.exists(path) || !fileSystem.metadata(path).isDirectory) {
            Log.v(TAG,"isFullyDownloaded $path not exists")
            return false
        }
        // when .downloaded files exists
        // this file is added after downloading and extracting the chapter
        else if (fileSystem.exists(dotDownloadedFile)) {
            Log.v(TAG,"$DOT_DOWNLOADED exists @ $dotDownloadedFile ")
            true
        }
        // count downloaded mp3 files to match number of chapter verses
        else {
            val mp3Files = fileSystem.list(path).filter { it.name.endsWith(".mp3") }
            Log.v(TAG,"dotDownloadedFile found-verses =  ${mp3Files.size} all = $verses")
            val allHasDownloaded = mp3Files.size == verses
            if (allHasDownloaded) markChapterAsDownloaded(path.toString())
            allHasDownloaded
        }
    }

    /**
     * Retrieves a list of chapter IDs that have been downloaded for a specific reciter.
     * @param reciterPath The absolute path to the reciter's directory.
     * @return A sorted list of downloaded chapter IDs.
     */
    suspend fun getDownloadedChapters(reciterPath: String): List<Int> {
        Log.v(TAG," check DownloadedChapters @$reciterPath")
        val listChapterIds = mutableListOf<Int>()
        val path = reciterPath.toPath()
        if (!fileSystem.exists(path) || !fileSystem.metadata(path).isDirectory) {
            return listChapterIds
        }

        fileSystem.list(path).forEach { dir: Path ->
            if (fileSystem.metadata(dir).isDirectory) {
                val chapterId = dir.name.toIntOrNull()
                val dotDownloadFile = "$dir/$DOT_DOWNLOADED".toPath()
                if (fileSystem.exists(dotDownloadFile) && chapterId != null) {
                    listChapterIds.add(chapterId)
                }
            }
        }
        Log.v(TAG,"${listChapterIds.size}")
        return listChapterIds.sorted()
    }

    /**
     * Downloads a file from the given URL to the specified path.
     * @param url The URL of the file to download.
     * @param path The absolute path where the downloaded file will be saved.
     * @param onProgress A callback function to report download progress.
     *                   It receives the downloaded amount (MB), total size (MB), and percentage.
     * @return True if the download was successful, false otherwise.
     */
    suspend fun downloadFile(
        url: String,
        path: String,
        onProgress: ((
            downloaded: Double,
            totalSize: Double,
            percent: Float
        ) -> Unit)? = null
    ): Boolean {
        val path = path.toPath()
        Log.v(TAG,"downloadFile $url @ $path")

        try {
            val response: HttpResponse = httpClient.get(url) {
                onDownload { bytesDownloaded, contentLength ->
                    val totalSize = (contentLength ?: -1L).toMb
                    val percent = if (contentLength != null && contentLength > 0) {
                        bytesDownloaded.toFloat() / contentLength.toFloat()
                    } else 0f
                    val downloaded = bytesDownloaded.toMb
                    //Log.v(TAG,"Progress: $downloaded/$totalSize - $percent")
                    onProgress?.invoke(downloaded, totalSize, percent)
                }
            }

            path.parent?.let {
                if (!fileSystem.exists(it)) {
                    fileSystem.createDirectories(it, mustCreate = false)
                }
            }

            // Actually start reading the body -> this triggers onDownload continuously
            fileSystem.write(path) {
                val channel = response.bodyAsChannel()
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer, 0, buffer.size)
                    if (read == -1) break
                    write(buffer, 0, read)
                }
            }

            // Ensure we finish at 100%
            response.contentLength()?.let { totalBytes ->
                onProgress?.invoke(totalBytes.toMb, totalBytes.toMb, 1f)
                Log.v(TAG,"Download completed")
                return true
            }
            return false
        } catch (e: Exception) {
            Log.v(TAG,"Error downloading file: ${e.message}")
            return false
        }
    }

    /**
     * Unzips a media file (chapter audio files).
     * @param outputPath The absolute path to the directory where the zip file is located and where the contents will be extracted.
     * The zip file is expected to be named `outputPath.zip`.
     * @return True if the unzipping was successful, false otherwise.
     */
    suspend fun unzipMediaFile(outputPath: String): Boolean {
        val destinationDir = outputPath.toPath()
        val zipFilePath = "${outputPath}.zip".toPath()
        Log.v(TAG,"Unzipping $zipFilePath ")
        val extracted = unzipFile(
            fileSystem = fileSystem,
            sourceZipPath = zipFilePath,
            destinationDirectory = destinationDir
        )
        Log.v(TAG,"Extracted  $extracted at $destinationDir")
        if (extracted) fileSystem.delete(zipFilePath)
        return extracted
    }

    /**
     * Marks a directory as fully downloaded by creating a ".downloaded" file inside it.
     * @param outputPath The absolute path to the directory to mark as downloaded.
     */
    suspend fun markChapterAsDownloaded(outputPath: String) {
        val donDownloadedFilePath = "$outputPath/$DOT_DOWNLOADED".toPath()
        try {
            // Create an empty file
            fileSystem.write(donDownloadedFilePath) {

            }
            Log.v(TAG,"Created $DOT_DOWNLOADED file at $donDownloadedFilePath")
        } catch (e: Exception) {
            Log.v(TAG,"Error creating $DOT_DOWNLOADED file: ${e.message}")
        }
    }

}