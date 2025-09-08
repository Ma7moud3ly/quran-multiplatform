package com.ma7moud3ly.quran.di

import com.ma7moud3ly.quran.managers.DownloadManager
import com.ma7moud3ly.quran.managers.SlidesManager
import com.ma7moud3ly.quran.managers.getSlidesManager
import com.ma7moud3ly.quran.platform.Platform
import com.ma7moud3ly.quran.platform.createSettings
import com.ma7moud3ly.quran.platform.getPlatform
import com.ma7moud3ly.quran.platform.getPlatformFileSystem
import com.ma7moud3ly.quran.platform.httpClientEngine
import com.ma7moud3ly.quran.platform.ioDispatcher
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import okio.FileSystem
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.ma7moud3ly.quran")
class AppModule {

    @Single
    fun provideSlidesManager(): SlidesManager = getSlidesManager()

    @Single
    fun providePlatform(): Platform = getPlatform()

    @Single
    fun provideDispatcher(): CoroutineDispatcher = ioDispatcher()

    @Single
    fun provideFileSystem(): FileSystem = getPlatformFileSystem()

    @Single
    fun provideSettings(): Settings = createSettings()

    @Single
    fun provideDownloadManager(): DownloadManager =
        DownloadManager(
            httpClient = HttpClient(httpClientEngine()),
            fileSystem = getPlatformFileSystem(),
            platform = getPlatform()
        )
}