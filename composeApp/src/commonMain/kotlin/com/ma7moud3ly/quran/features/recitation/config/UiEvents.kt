package com.ma7moud3ly.quran.features.recitation.config

import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.ScreenMode
import com.ma7moud3ly.quran.model.Reciter

sealed interface ConfigEvents {
    data object OnBack : ConfigEvents
    data object PickChapters : ConfigEvents
    data object PickReciters : ConfigEvents
    data object ClearReciters : ConfigEvents
    data class RemoveReciter(val reciter: Reciter) : ConfigEvents
    data class SelectChapter(val chapter: Chapter) : ConfigEvents
    data class InitRecitation(val mode: ScreenMode) : ConfigEvents
}

sealed interface RecitationEvents {
    data object OnBack : RecitationEvents
    data class ChaptersDialog(val chapterId: Int?) : RecitationEvents
    data class RecitersDialog(val selectMultiple: Boolean) : RecitationEvents

    data object ConfirmDownload : RecitationEvents
    data object StartOnline : RecitationEvents
    data object StartLocally : RecitationEvents
}

val RecitationEvents?.playOnline: Boolean get() = this is RecitationEvents.StartOnline
