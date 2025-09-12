package com.ma7moud3ly.quran.model

import com.ma7moud3ly.quran.data.repository.DownloadsRepository
import com.ma7moud3ly.quran.data.repository.RecitationRepository
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.managers.VersesManager
import com.ma7moud3ly.quran.managers.getSlidesManager
import com.ma7moud3ly.quran.platform.getPlatform
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
    selectedVerse = 1,
    lastVerseNumber = 6,
)

internal val testRecitationWithReelMode = Recitation(
    chapter = testChapter,
    reciters = listOf(testReciter),
    selectedVerse = 1,
    lastVerseNumber = 6,
    reelMode = true
)

internal val testRecitationState = RecitationState(
    firstVerse = 1,
    lastVerse = 7,
    reelMode = true,
    playbackMode = PlaybackMode.Single,
    playInBackground = false,
    canChangeChapter = true,
    canChangeReciter = true,
    canChangeVerse = true
)

internal val testReadingSettings = ReadingSettings()

internal val testSlidesManager = getSlidesManager()
internal val testRecitationSettings = RecitationSettings()

private class TestRecitationRepImpl(
    private val recitation: Recitation
) : RecitationRepository {
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
)

fun testMediaPlayerManagerInReelMode() = MediaPlayerManager(
    TestRecitationRepImpl(testRecitationWithReelMode),
    TestDownloadsRepositoryImpl(),
    getPlatform()
)


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