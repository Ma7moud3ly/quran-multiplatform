package com.ma7moud3ly.quran.data.impl

import androidx.compose.ui.graphics.Color
import com.ma7moud3ly.quran.data.repository.BackgroundsRepository
import com.ma7moud3ly.quran.model.PreferenceKeys.TV_BACKGROUND_ID
import com.ma7moud3ly.quran.model.TvBackground
import com.ma7moud3ly.quran.model.UserVideo
import com.ma7moud3ly.quran.platform.BackgroundVideos
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.Platform
import com.ma7moud3ly.quran.platform.getAppLocalDataStoragePath
import com.ma7moud3ly.quran.platform.getVideoThumbnail
import com.ma7moud3ly.quran.platform.isJvm
import com.russhwolf.settings.Settings
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single

/**
 * Implementation of [BackgroundsRepository] that manages the TV backgrounds.
 *
 * @param backgroundVideos The available background videos.
 * @param platformSettings The platform settings.
 * @param ioDispatcher The coroutine dispatcher for I/O operations.
 * @param platform The current platform.
 * @param fileSystem The file system.
 */
@Single
class BackgroundsRepositoryImpl(
    private val backgroundVideos: BackgroundVideos,
    private val platformSettings: Settings,
    private val ioDispatcher: CoroutineDispatcher,
    private val platform: Platform,
    private val fileSystem: FileSystem
) : BackgroundsRepository {

    private val _backgroundsFlow = MutableStateFlow(listOf<TvBackground>())

    /**
     * A flow of the available TV backgrounds.
     */
    override val backgroundsFlow: Flow<List<TvBackground>> = _backgroundsFlow.asStateFlow()

    private val _selectedBackgroundFlow = MutableStateFlow<TvBackground?>(null)

    /**
     * A flow of the currently selected TV background.
     */
    override val selectedBackgroundFlow: Flow<TvBackground?> = _selectedBackgroundFlow.asStateFlow()


    /**
     * Retrieves a list of user-added backgrounds from the file system.
     *
     * This function checks for the existence of directories for user-uploaded videos and their thumbnails.
     * If they don't exist, it creates them. It then scans these directories to build a list of
     * [TvBackground] objects.
     *
     * It performs a sanity check: if the number of video files and thumbnail files do not match,
     * it clears both directories to prevent inconsistencies and returns an empty list.
     *
     * For each video file, it looks for a corresponding thumbnail (based on the filename) and,
     * if found, creates a [TvBackground] object representing the user's custom background.
     *
     * @return A list of [TvBackground] objects corresponding to the user's custom backgrounds.
     */
    private fun getUserBackgrounds(): List<TvBackground> {
        if (tvVideosPath.exists().not()) {
            fileSystem.createDirectories(tvVideosPath)
        }
        if (tvThumbnailsPath.exists().not()) {
            fileSystem.createDirectories(tvThumbnailsPath)
        }
        val backgrounds = mutableListOf<TvBackground>()
        val videos = fileSystem.list(tvVideosPath)
        val thumbnails = fileSystem.list(tvThumbnailsPath)
        if (videos.size != thumbnails.size) {
            videos.forEach { fileSystem.delete(it) }
            thumbnails.forEach { fileSystem.delete(it) }
            Log.v(TAG, "clean-videos")
            return emptyList()
        }
        videos.forEach { videoPath ->
            val id = videoPath.name.substringBefore(".")
            val thumbnail = "${tvThumbnailsPath}/$id.png".toPath()
            if (thumbnail.exists()) {
                val tvBackground = TvBackground(
                    id = id,
                    color = Color.Black,
                    background = Color.White.copy(alpha = 0.5f),
                    video = UserVideo(
                        videoPath = videoPath.toString(),
                        thumbnailPath = thumbnail.toString()
                    ),
                    canRemove = true
                )
                backgrounds.add(tvBackground)
                Log.v(TAG, "files: $tvBackground")
            }
        }
        return backgrounds
    }

    /**
     * Initializes the backgrounds by loading them from the file system and updating the flows.
     */
    override suspend fun initBackgrounds() {
        withContext(ioDispatcher) {
            val backgrounds = mutableListOf<TvBackground>()
            val appBackgrounds = backgroundVideos.backgrounds
            val userBackgrounds = getUserBackgrounds()
            backgrounds.addAll(userBackgrounds)
            backgrounds.addAll(appBackgrounds)
            _backgroundsFlow.value = backgrounds
            _selectedBackgroundFlow.value = getSelectedBackground()
        }
    }

    /**
     * Returns the current list of backgrounds.
     */
    override fun getBackgrounds() = _backgroundsFlow.value

    /**
     * Returns the currently selected background.
     */
    override fun getSelectedBackground(): TvBackground {
        val id = platformSettings.getString(TV_BACKGROUND_ID, "")
        return getBackgrounds().firstOrNull() { it.id == id } ?: getBackgrounds().first()
    }

    /**
     * Selects the given background.
     *
     * @param tvBackground The background to select.
     */
    override fun selectBackground(tvBackground: TvBackground) {
        Log.v(TAG, "set-last-background: ${tvBackground.id}")
        _selectedBackgroundFlow.value = tvBackground
        platformSettings.putString(TV_BACKGROUND_ID, tvBackground.id)
    }

    /**
     * Opens a file picker to allow the user to select a new background video.
     *
     * This function handles different file types based on the platform:
     * - On JVM, it opens an image picker.
     * - On other platforms, it opens a video picker.
     *
     * After the user selects a file, the function:
     * 1. Reads the file's byte data.
     * 2. Generates a unique ID and file paths for the new video and its thumbnail.
     * 3. Saves the video file to the application's local storage.
     * 4. Generates and saves a thumbnail for the video.
     * 5. Creates a new [TvBackground] object for the added video.
     * 6. Updates the list of available backgrounds and sets the new background as the currently selected one.
     *
     * All file I/O operations are performed on the `ioDispatcher`. If the user cancels the
     * file selection, the function returns without making any changes.
     */
    override suspend fun addNewBackground() {
        withContext(ioDispatcher) {
            val mediaFile: PlatformFile = FileKit.openFilePicker(
                type = if (platform.isJvm) FileKitType.Image
                else FileKitType.Video
            ) ?: return@withContext

            val bytes: ByteArray = mediaFile.readBytes()

            val id = getTimeMillis()
            val mediaFileName = "$id.${mediaFile.extension}"
            val thumbnailFileName = "$id.png"
            val videoPath = "$tvVideosPath/$mediaFileName".toPath()
            val thumbnailPath = "$tvThumbnailsPath/$thumbnailFileName".toPath()

            fileSystem.write(videoPath) {
                write(bytes)
            }
            if (videoPath.exists()) {
                val thumbnail = getVideoThumbnail(videoPath.toString(), 1000)
                if (thumbnail != null) {
                    fileSystem.write(thumbnailPath) {
                        write(thumbnail)
                    }
                }
            }
            if (videoPath.exists() && thumbnailPath.exists()) {
                Log.v(TAG, "save-video-at: $videoPath")
                Log.v(TAG, "save-thumbnail-at: $thumbnailPath")

                val tvBackground = TvBackground(
                    id = id.toString(),
                    color = Color.Black,
                    background = Color.White.copy(alpha = 0.5f),
                    video = UserVideo(
                        videoPath = videoPath.toString(),
                        thumbnailPath = thumbnailPath.toString()
                    ),
                    canRemove = true
                )
                val backgrounds = getBackgrounds().toMutableList()
                backgrounds.add(0, tvBackground)
                _backgroundsFlow.value = backgrounds

                selectBackground(tvBackground)
            }
        }
    }

    /**
     * Removes the given background.
     *
     * @param tvBackground The background to remove.
     */
    override suspend fun removeBackground(tvBackground: TvBackground) {
        when (val video = tvBackground.video) {
            is UserVideo -> {
                val videoPath = video.path.toPath()
                val thumbnailPath = video.thumbnailPath.toPath()
                if (fileSystem.exists(videoPath)) fileSystem.delete(videoPath)
                if (fileSystem.exists(thumbnailPath)) fileSystem.delete(thumbnailPath)
                initBackgrounds()
            }

            else -> {}
        }

    }

    private val tvVideosPath get() = (getAppLocalDataStoragePath() + "/tv-videos").toPath()
    private val tvThumbnailsPath get() = (getAppLocalDataStoragePath() + "/tv-thumbnail").toPath()

    fun Path.exists(): Boolean = fileSystem.exists(this)

    companion object {
        private const val TAG = "BackgroundsRepository"
    }
}
