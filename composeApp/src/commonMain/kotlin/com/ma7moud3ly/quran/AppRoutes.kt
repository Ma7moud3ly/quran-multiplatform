package com.ma7moud3ly.quran

import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.Serializable

object AppRoutes {
    @Serializable
    object HomeScreen

    @Serializable
    object SearchScreen

    @Serializable
    data class ReadingScreen(
        val chapterId: Int,
        val selectedVerseId: Int? = null
    )


    data object Recitation {
        @Serializable
        data class Config(
            val verseId: Int? = null,
            val chapterId: Int? = null,
            val reciterId: String? = null,
            val canChangeChapter: Boolean = true,
            val canChangeReciter: Boolean = true,
            val canChangeVerse: Boolean = true,
            val timestamp: Long = getTimeMillis()
        )

        @Serializable
        data object Playback

        @Serializable
        data object Download {
            @Serializable
            data object Confirm
        }

        @Serializable
        data object NotAvailable

        object Search {
            @Serializable
            data class Chapter(val chapterId: Int? = null)

            @Serializable
            data class Reciter(val reciterId: String?, val filter: Boolean)
        }
    }


    @Serializable
    data class SettingsScreen(val reading: Boolean = true)

}