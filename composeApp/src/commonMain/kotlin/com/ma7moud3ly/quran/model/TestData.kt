package com.ma7moud3ly.quran.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ma7moud3ly.quran.data.repository.DownloadsRepository
import com.ma7moud3ly.quran.data.repository.RecitationRepository
import com.ma7moud3ly.quran.data.repository.BackgroundsRepository
import com.ma7moud3ly.quran.managers.BackgroundsManager
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.managers.VersesManager
import com.ma7moud3ly.quran.platform.getPlatform
import com.ma7moud3ly.quran.platform.getPlaybackVideos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal val testChapter = Chapter(
    id = 2,
    name = "الفاتحة",
    type = "meccan",
    verses = listOf(
        Verse(
            id = 1,
            text = "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        ),
        Verse(
            id = 2,
            text = "ٱلۡحَمۡدُ لِلَّهِ رَبِّ ٱلۡعَٰلَمِينَ",
        ),
        Verse(
            id = 3,
            text = "ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        ), Verse(
            id = 4,
            text = "ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        ), Verse(
            id = 5,
            text = "ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        ), Verse(
            id = 6,
            text = "ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        ), Verse(
            id = 7,
            text = "ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        ), Verse(
            id = 8,
            text = "ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        ), Verse(
            id = 9,
            text = "ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        )
    )
)

internal val testDownloadedChapter = listOf(
    testChapter,
    testChapter,
    testChapter
)

internal val testReciter = Reciter(
    id = "1",
    name = "أبو بكر الشاطري"
)


internal val testRecitation = Recitation(
    chapter = testChapter,
    reciters = listOf(testReciter),
    firstVerse = 1,
    lastVerse = 6,
)

internal val testRecitationWithReelMode = Recitation(
    chapter = testChapter,
    reciters = listOf(testReciter),
    firstVerse = 1,
    lastVerse = 6,
    reelMode = true
)

internal val testRecitationState = RecitationState(
    firstVerse = 1,
    lastVerse = 7,
    reelMode = true,
    playInBackground = false,
    canChangeChapter = true,
    canChangeReciter = true,
    canChangeVerse = true,
    playbackMode = PlaybackMode.Sequential
)

internal val testReadingSettings = ReadingSettings()

internal val testBackgroundsManager = BackgroundsManager(
    backgroundsRepository = TestBackgroundsRepoImpl()
)

private class TestBackgroundsRepoImpl() : BackgroundsRepository {
    override val backgroundsFlow: Flow<List<TvBackground>>
        get() = flow { getPlaybackVideos().backgrounds }

    override val selectedBackgroundFlow: Flow<TvBackground>
        get() = flow { TvBackground() }

    override fun getBackgrounds(): List<TvBackground> {
        return getPlaybackVideos().backgrounds
    }

    override suspend fun initBackgrounds() {

    }

    override suspend fun addNewBackground() {
    }

    override suspend fun removeBackground(tvBackground: TvBackground) {
    }

    override fun selectBackground(tvBackground: TvBackground) {
    }

    override fun getSelectedBackground() = TvBackground()

}

internal val testRecitationSettings = RecitationSettings()

private class TestRecitationRepImpl(
    private val recitation: Recitation
) : RecitationRepository {
    override val recitationState: MutableState<RecitationState> = mutableStateOf(RecitationState())
    override val recitationFlow: Flow<Recitation?> get() = flow { recitation }
    override fun getRecitation() = recitation
}

internal val testVersesManager = VersesManager(
    verses = testChapter.verses,
    initialVerseId = 1
)

fun testMediaPlayerManager() = MediaPlayerManager(
    TestRecitationRepImpl(testRecitation),
    TestDownloadsRepositoryImpl(),
    getPlatform()
).apply {
    initPlayBack()
}

fun testMediaPlayerManagerInReelMode() = MediaPlayerManager(
    TestRecitationRepImpl(testRecitationWithReelMode),
    TestDownloadsRepositoryImpl(),
    getPlatform()
).apply {
    initPlayBack()
}


private class TestDownloadsRepositoryImpl : DownloadsRepository {
    override val platformSupportDownloading: Boolean
        get() = true
    override val downloadProgress: Flow<DownloadProgress>
        get() = flow { DownloadProgress() }
    override val downloadComplete: Flow<DownloadResult>
        get() = flow { DownloadResult() }

    override suspend fun isFullyDownloaded(
        path: String,
        verses: Int
    ): Boolean {
        return false
    }

    override suspend fun getDownloadedChapters(reciterPath: String): List<Int> {
        return listOf()
    }

    override suspend fun downloadChapter(
        downloadId: String,
        url: String,
        outputPath: String
    ) {

    }

    override fun toMediaFile(path: String, link: String) = MediaFile("", true)
    override suspend fun downloadVerse(mediaFile: MediaFile) = MediaFile("", true)

}