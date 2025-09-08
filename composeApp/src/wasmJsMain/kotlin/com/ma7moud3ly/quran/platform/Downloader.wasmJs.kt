package com.ma7moud3ly.quran.platform

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js
import okio.FileSystem
import okio.Path
import okio.fakefilesystem.FakeFileSystem

actual fun httpClientEngine(): HttpClientEngineFactory<*> = Js // Ktor's JS engine

actual fun getPlatformFileSystem(): FileSystem {
    return FakeFileSystem()
}

actual fun getAppLocalDataStoragePath(): String {
    // For FakeFileSystem, this path is conceptual as it's all in memory.
    // If you had a persistent FS, this would point to a real location.
    return "/appdata" // Or any root path you define for your FakeFileSystem structure
}


actual suspend fun unzipFile(
    fileSystem: FileSystem,
    sourceZipPath: Path,
    destinationDirectory: Path
): Boolean {
    return false
}