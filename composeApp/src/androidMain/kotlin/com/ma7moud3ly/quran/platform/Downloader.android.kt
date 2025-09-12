package com.ma7moud3ly.quran.platform

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import kotlinx.io.IOException
import okio.FileSystem
import okio.Path
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

actual fun httpClientEngine(): HttpClientEngineFactory<*> = Android

actual fun getPlatformFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getAppLocalDataStoragePath(): String {
    try {
        return AndroidApp.requireContext().filesDir.absolutePath
    } catch (e: UninitializedPropertyAccessException) {
        e.printStackTrace()
        throw IllegalStateException(
            "ApplicationContext has not been initialized. " +
                    "Ensure MyApplication.appContext is set before calling this function."
        )
    }
}

actual suspend fun unzipFile(
    fileSystem: FileSystem,
    sourceZipPath: Path,
    destinationDirectory: Path
): Boolean {
    return try {
        val destinationDir = File(destinationDirectory.toString())
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
        println("Error unzipping file on Android: ${e.message}")
        // You might want to log the exception or handle it more gracefully
        false
    }
}
