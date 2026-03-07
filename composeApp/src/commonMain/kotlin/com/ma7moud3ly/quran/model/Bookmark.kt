package com.ma7moud3ly.quran.model

import io.ktor.util.date.getTimeMillis
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class Bookmark(
    val verseId: Int,
    val chapterId: Int,
    val date: String = getCurrentFormattedTime(),
    val timeStamp: Long = getTimeMillis(),
) {
    val id get() = chapterId.toString()

    companion object {
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
    }
}

