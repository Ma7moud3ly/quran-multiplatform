package com.ma7moud3ly.quran.platform

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import okio.FileSystem
import okio.Path
import platform.Foundation.*

actual fun httpClientEngine(): HttpClientEngineFactory<*> = Darwin

actual fun getPlatformFileSystem(): FileSystem = FileSystem.SYSTEM

actual fun getAppLocalDataStoragePath(): String {
    val paths = NSSearchPathForDirectoriesInDomains(
        NSDocumentDirectory,
        NSUserDomainMask,
        true
    )
    val documentsDirectory = paths.firstOrNull() as? String
        ?: throw IllegalStateException("Unable to access iOS Documents directory")
    return documentsDirectory
}

actual suspend fun unzipFile(
    fileSystem: FileSystem,
    sourceZipPath: Path,
    destinationDirectory: Path
): Boolean {
   return false
}
