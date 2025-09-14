package com.ma7moud3ly.quran.features.reading.modes

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.managers.VersesManager
import com.ma7moud3ly.quran.model.AppFont
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.model.testChapter
import com.ma7moud3ly.quran.model.testReadingSettings
import com.ma7moud3ly.quran.model.testVersesManager
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs


@Preview
@Composable
private fun SectionMultiVersesPreview() {
    AppTheme(darkTheme = true) {
        Surface {
            SectionMultiVerses(
                chapter = testChapter,
                versesManager = testVersesManager,
                font = testReadingSettings.font,
                onCopyVerse = {},
            )
        }
    }
}

@Preview
@Composable
private fun SectionMultiVersesPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface {
            SectionMultiVerses(
                chapter = testChapter,
                versesManager = testVersesManager,
                font = testReadingSettings.font,
                onCopyVerse = {}
            )
        }
    }
}

private const val TAG = "SectionMultiVerses"

@OptIn(FlowPreview::class)
@Composable
internal fun SectionMultiVerses(
    chapter: Chapter,
    font: AppFont,
    versesManager: VersesManager,
    showDivider: Boolean = true,
    onCopyVerse: () -> Unit,
    onNextChapter: (() -> Unit)? = null,
    onPreviousChapter: (() -> Unit)? = null
) {
    val coroutine = rememberCoroutineScope()
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    var selectedVerse by remember { mutableStateOf<Verse?>(null) }
    val showBasmeallah = remember(chapter) {
        chapter.id != 1 && chapter.id != 9 && chapter.verses.firstOrNull()?.id == 1
    }

    LaunchedEffect(versesManager) {
        val offset = if (showBasmeallah) 1 else 0
        versesManager.selectedVerse.collect { verse ->
            selectedVerse = verse
            val selectedVerseIndex = versesManager.selectedVerseIndex + offset
            val firstVisibleItemIndex = listState.firstVisibleItemIndex
            val visibleItemsCount = listState.layoutInfo.visibleItemsInfo.size - 2
            val distance = abs(selectedVerseIndex - firstVisibleItemIndex)
            if (selectedVerseIndex < firstVisibleItemIndex ||
                distance >= visibleItemsCount
            ) coroutine.launch {
                Log.v(TAG, " ---Scroll to show a selected verse")
                if (distance > 15) listState.scrollToItem(selectedVerseIndex, -100)
                else listState.animateScrollToItem(selectedVerseIndex, -100)
            }
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
    ) {
        if (showBasmeallah) item {
            TextBasmala(fontSize = font.fontSize)
            if (showDivider) HorizontalDivider()
        }
        items(chapter.verses) { verse ->
            ItemVerse(
                verse = verse,
                font = font,
                textAlign = TextAlign.Justify,
                onCopyVerse = onCopyVerse,
                current = { verse.id == selectedVerse?.id },
                onClick = { versesManager.selectVerse(verse) }
            )
            if (showDivider) HorizontalDivider()
        }
        if (onNextChapter != null && onPreviousChapter != null) {
            item {
                SectionNavigation(
                    chapter = chapter,
                    onNext = onNextChapter,
                    onPrevious = onPreviousChapter
                )
            }
        }
    }
}

@Composable
internal fun ItemVerse(
    verse: Verse,
    current: () -> Boolean,
    font: AppFont,
    autoSize: Boolean = false,
    textAlign: TextAlign = TextAlign.Justify,
    onClick: () -> Unit,
    onCopyVerse: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val interactionSource = remember { MutableInteractionSource() }

    fun copyVerse() {
        val verseContent = buildAnnotatedString {
            append(verse.text)
            append(" ")
            append("(${verse.id})")
        }
        clipboard.setText(verseContent)
        onCopyVerse()
        onClick()
    }
    Surface(
        color = if (current()) MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
        else Color.Transparent
    ) {
        BasicText(
            text = formatVerse(verse),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily(Font(font.fontType)),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = textAlign,
                fontSize = font.fontSize,
                lineHeight = font.lineHeight,
                letterSpacing = font.letterSpacing
            ),
            autoSize = if (autoSize) TextAutoSize.StepBased(
                minFontSize = 5.sp,
                maxFontSize = font.fontSize
            ) else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = ::copyVerse,
                    interactionSource = interactionSource,
                    indication = null
                )
        )
    }
}

@Composable
fun formatVerse(verse: Verse): AnnotatedString {
    return buildAnnotatedString {
        append(verse.text)
        append("\u00A0") // This is the Unicode character for Non-Breaking Space
        withStyle(style = SpanStyle(fontFamily = hafsSmartFamily())) {
            append(verse.id.asVerseNumber())
        }
    }
}
