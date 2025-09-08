package com.ma7moud3ly.quran.features.reading.modes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.platform.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun detectScrollPositionByVerse(
    verse: Verse,
    chapterText: String,
    textLayoutResult: TextLayoutResult
): Int = withContext(Dispatchers.Default) {
    val index = chapterText.indexOf(verse.text)
    if (index != -1) {
        try {
            val rect = textLayoutResult.getBoundingBox(index)
            rect.top.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    } else 0
}

suspend fun detectVerseByScrollPosition(
    chapter: Chapter,
    chapterText: String,
    scrollPosition: Int,
    textLayoutResult: TextLayoutResult
): Verse? = withContext(Dispatchers.Default) {
    // Get the character offset at the given scrollPosition
    val characterOffset = textLayoutResult.getOffsetForPosition(
        Offset(0f, scrollPosition.toFloat())
    )

    val substringStart = (characterOffset - 15).coerceAtLeast(0)
    val substringEnd = (characterOffset + 15).coerceAtMost(chapterText.length)

    Log.v(
        "SectionContinuesVerses", "substringStart: $substringStart, " +
                "substringEnd: $substringEnd," +
                " chapterText.length: ${chapterText.length}"
    )

    val verse = if (substringStart < substringEnd && chapterText.length > substringEnd) {
        val currentSubstring = chapterText.substring(substringStart, substringEnd)
        //println("SectionContinuesVerses currentSubstring: $currentSubstring")
        chapter.verses.firstOrNull { it.text.contains(currentSubstring) }
    } else null

    verse
}


fun linkAnnotation(
    verseId: Int,
    onClick: (Int) -> Unit
) = LinkAnnotation.Clickable(
    tag = verseId.toString(),
    styles = TextLinkStyles(
        pressedStyle = SpanStyle(
            color = Color.Yellow,
        ),
    )
) {
    onClick(verseId)
}