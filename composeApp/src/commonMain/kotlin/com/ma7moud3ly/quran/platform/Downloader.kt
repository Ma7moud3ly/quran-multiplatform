package com.ma7moud3ly.quran.platform

import io.ktor.client.engine.HttpClientEngineFactory
import okio.FileSystem
import okio.Path
/**
 * Returns an [HttpClientEngineFactory] for the current platform.
 */
expect fun httpClientEngine(): HttpClientEngineFactory<*>

/**
 * Returns a [FileSystem] instance for the current platform.
 */
expect fun getPlatformFileSystem(): FileSystem

/**
 * Returns the absolute path to the application's local data storage directory.
 * This is where the application can store private data.
 *
 * For example:
 * - **Android:** `/data/user/0/com.ma7moud3ly.quran/files`
 * - **iOS:** `/var/mobile/Containers/Data/Application/<UUID>/Documents`
 * - **JVM:** `/home/user/.local/share/com.ma7moud3ly.quran` (or platform-specific equivalent)
 */
expect fun getAppLocalDataStoragePath(): String

/**
 * Unzips a ZIP file from the given [sourceZipPath] to the specified [destinationDirectory].
 *
 * @param fileSystem The [FileSystem] to use for file operations.
 * @param sourceZipPath The [Path] to the source ZIP file.
 * @param destinationDirectory The [Path] to the directory where the ZIP file should be extracted.
 * @return `true` if the unzipping was successful, `false` otherwise.
 *
 * **Platform Implementation Notes:**
 * - **Android/JVM:** Uses `java.util.zip.ZipInputStream`.
 */
expect suspend fun unzipFile(
    fileSystem: FileSystem,
    sourceZipPath: Path,
    destinationDirectory: Path
): Boolean
