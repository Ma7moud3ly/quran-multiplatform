package com.ma7moud3ly.quran.platform

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import kotlinx.io.IOException
import okio.FileSystem
import okio.Path
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

actual fun httpClientEngine(): HttpClientEngineFactory<*> = CIO

actual fun getPlatformFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getAppLocalDataStoragePath(): String {
    val userHome = System.getProperty("user.home")
    val appName = "quran-app"
    val appDataDir = File(userHome, File.separator + appName)

    if (!appDataDir.exists()) {
        try {
            appDataDir.mkdirs() // Java File API way, simpler here for directory creation
        } catch (e: SecurityException) {
            e.printStackTrace()
            throw IllegalStateException(
                "Could not create app data directory due to security restrictions: ${appDataDir.absolutePath}",
                e
            )
        } catch (e: Exception) {
            throw IllegalStateException(
                "Could not create app data directory: ${appDataDir.absolutePath}",
                e
            )
        }
    }
    if (!appDataDir.isDirectory) {
        throw IllegalStateException("App data storage path exists but is not a directory: ${appDataDir.absolutePath}")
    }
    return appDataDir.absolutePath
}

actual suspend fun unzipFile(
    fileSystem: FileSystem,
    sourceZipPath: Path,
    destinationDirectory: Path
): Boolean {
    return try {
        val destinationDir = File(destinationDirectory.toString())
        println("Unzipping file: $sourceZipPath to $destinationDir")
        ZipInputStream(
            File(sourceZipPath.toString()).inputStream().buffered()
        ).use { zipInputStream ->
            var entry = zipInputStream.nextEntry
            while (entry != null) {
                val entryFile = File(destinationDir, entry.name)
                // Ensure parent directories are created, especially for nested structures in zip
                if (entry.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    entryFile.parentFile?.mkdirs()
                    FileOutputStream(entryFile).use { fileOutputStream ->
                        zipInputStream.copyTo(fileOutputStream)
                    }
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
        }
        true
    } catch (e: IOException) {
        println("Error unzipping file: ${e.message}")
        // You might want to log the exception or handle it more gracefully
        false
    }
}
