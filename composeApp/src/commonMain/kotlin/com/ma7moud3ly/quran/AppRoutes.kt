package com.ma7moud3ly.quran

import androidx.navigation.NavBackStackEntry
import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.text.startsWith

object AppRoutes {
    @Serializable
    @SerialName("?")
    data object HomeScreen

    @Serializable
    @SerialName("search")
    data object SearchScreen

    @Serializable
    @SerialName("reading")
    data class ReadingScreen(
        val chapterId: Int,
        val verseId: Int? = null
    )

    object Recitation {

        @Serializable
        @SerialName("recitation")
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
        @SerialName("playback")
        data object Playback

        @Serializable
        @SerialName("download")
        data object Download {
            @Serializable
            @SerialName("confirm-download")
            data object Confirm
        }

        object Pick {
            @Serializable
            @SerialName("chapters")
            data class Chapter(val chapterId: Int? = null)

            @Serializable
            @SerialName("reciters")
            data class Reciter(val selectMultiple: Boolean)
        }
    }


    @Serializable
    @SerialName("settings")
    data class SettingsScreen(val reading: Boolean = true)
}


/**
 * Checks if the route of the [NavBackStackEntry] destination matches the given serial name of KClass.
 *
 * @param kClass The KClass to match against.
 * @return `true` if the route matches, `false` otherwise.
 */
@OptIn(InternalSerializationApi::class)
fun NavBackStackEntry.match(kClass: KClass<*>): Boolean {
    val route = destination.route.orEmpty()
    val serialName = kClass.serializer().descriptor.serialName
    return route.startsWith(serialName)
}