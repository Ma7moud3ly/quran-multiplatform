package com.ma7moud3ly.quran.model

import io.ktor.util.date.getTimeMillis
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class History(
    val timeStamp: Long = getTimeMillis(),
    val date: String = getCurrentFormattedTime(),
    val type: Int = READING,
    val verseId: Int,
    val chapterId: Int,
    val chapterName: String,
    val reciterId: String? = null,
    val reciterName: String? = null,
    private val screenMode: Int? = null,
    val reelMode: Boolean = false,
    val playInBackground: Boolean = false,
    val playLocally: Boolean = false,
    val shuffleReciters: Boolean = false
) {
    val id: String
        get() = if (isReading) chapterId.toString()
        else "$chapterId-$reciterId-$screenMode"

    val screenModeName: ScreenMode
        get() = if (screenMode == 1) ScreenMode.Normal
        else ScreenMode.Tv

    val isNormalScreen: Boolean get() = screenMode == 1

    val isReading: Boolean get() = type == 1

    companion object {

        @OptIn(ExperimentalTime::class)
        private fun getCurrentFormattedTime(): String {
            val now = Clock.System.now()
            val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
            val hour = localDateTime.hour
            val minute = localDateTime.minute
            val day = localDateTime.day
            val month = localDateTime.month.number
            val year = localDateTime.year
            val period = if (hour < 12) "ص" else "م"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val minuteString = if (minute < 10) "0$minute" else minute.toString()
            return "$displayHour:$minuteString $period  $day-$month-$year"
        }

        const val READING = 1
        const val LISTENING = 2
    }
}