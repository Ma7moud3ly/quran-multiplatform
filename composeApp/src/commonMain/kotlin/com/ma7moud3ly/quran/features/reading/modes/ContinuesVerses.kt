package com.ma7moud3ly.quran.features.reading.modes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ma7moud3ly.quran.managers.VersesManager
import com.ma7moud3ly.quran.model.AppFont
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.model.testChapter
import com.ma7moud3ly.quran.model.testReadingSettings
import com.ma7moud3ly.quran.model.testVersesManager
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MySurface
import com.ma7moud3ly.quran.ui.SelectionBox
import com.ma7moud3ly.quran.ui.basmeallahFontFamily
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import com.ma7moud3ly.quran.ui.suraNameFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SectionContinuesVersesPreview() {
    AppTheme(darkTheme = true) {
        Surface {
            SectionContinuesVerses(
                versesManager = testVersesManager,
                chapter = testChapter,
                font = testReadingSettings.font,
                onClick = {},
                onNextChapter = {},
                onPreviousChapter = {}
            )
        }
    }
}

@Preview
@Composable
private fun SectionContinuesVersesPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface {
            SectionContinuesVerses(
                versesManager = testVersesManager,
                chapter = testChapter,
                font = testReadingSettings.font,
                onClick = {},
                onNextChapter = {},
                onPreviousChapter = {}
            )
        }
    }
}

private const val TAG = "SectionContinuesVerses"

@Composable
internal fun SectionContinuesVerses(
    versesManager: VersesManager,
    chapter: Chapter,
    font: AppFont,
    onClick: () -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
) {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val selectedVerse by versesManager.selectedVerse.collectAsState(null)
    val hafsFont = SpanStyle(fontFamily = hafsSmartFamily())
    val lineHeight by remember(font) { derivedStateOf { font.getLineHeight() } }
    val showBasmeallah = chapter.id != 1 && chapter.id != 9
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var verseAnnotated by remember(AnnotatedString) {
        mutableStateOf(buildAnnotatedString { "" })
    }

    LaunchedEffect(chapter) {
        if (chapter.verses.isNotEmpty()) {
            val newAnnotatedString = withContext(Dispatchers.Default) {
                buildAnnotatedString {
                    for (verse in chapter.verses) {
                        append(verse.text)
                        append(" ")
                        withStyle(hafsFont) { // Make sure hafsFont is safe to access off-main thread
                            append(verse.id.asVerseNumber())
                        }
                        append(" ")
                    }
                }
            }
            delay(10)
            verseAnnotated = newAnnotatedString
        }
    }

    selectedVerse?.let { selectedVerse ->
        LaunchedEffect(
            selectedVerse,
            textLayoutResult
        ) {
            if (selectedVerse.id > 1 && textLayoutResult != null) {
                val scrollPosition = detectScrollPositionByVerse(
                    chapterText = verseAnnotated.text,
                    verse = selectedVerse,
                    textLayoutResult = textLayoutResult!!
                )
                withContext(Dispatchers.Main) {
                    scrollState.scrollTo(scrollPosition)
                    Log.v(TAG, "scroll- to verse" + " ${selectedVerse.id} at  $scrollPosition")
                }
            }
        }
    }

    DisposableEffect(LocalLifecycleOwner) {
        onDispose {
            coroutineScope.launch(NonCancellable) {
                val verse = detectVerseByScrollPosition(
                    chapter = chapter,
                    chapterText = verseAnnotated.text,
                    scrollPosition = scrollState.value,
                    textLayoutResult = textLayoutResult!!
                )
                if (verse != null) {
                    versesManager.selectVerse(verse)
                    Log.v(TAG, "last-verse - ${verse.text}")
                }
            }
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    Box {
        if (verseAnnotated.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(30.dp)
                    .zIndex(2f)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = interactionSource
                )
        ) {
            if (showBasmeallah) {
                TextBasmala(fontSize = font.fontSize)
            }
            SelectionBox {
                Text(
                    text = verseAnnotated,
                    fontFamily = FontFamily(
                        Font(
                            font.fontType,
                            FontWeight.Normal
                        )
                    ),
                    fontSize = font.fontSize,
                    lineHeight = lineHeight,
                    textAlign = TextAlign.Justify,
                    color = MaterialTheme.colorScheme.onPrimary,
                    onTextLayout = { textLayoutResult = it }
                )
            }
            Spacer(Modifier.weight(1f))
            SectionNavigation(
                chapter = chapter,
                onNext = onNextChapter,
                onPrevious = onPreviousChapter
            )
        }
    }
}

@Composable
internal fun TextBasmala(fontSize: TextUnit) {
    Text(
        text = "Q",
        fontFamily = basmeallahFontFamily(),
        fontSize = (fontSize.value + 120).sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize(),
    )
}


@Composable
internal fun SectionNavigation(
    chapter: Chapter,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        chapter.previousChapterName()?.let { name ->
            NavigationButton(
                text = name,
                onClick = onPrevious
            )
        }
        Spacer(Modifier.weight(1f))
        chapter.nextChapterName()?.let { name ->
            NavigationButton(
                text = name,
                onClick = onNext
            )
        }
    }
}

@Composable
private fun NavigationButton(
    text: String,
    onClick: () -> Unit
) {
    MySurface(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = suraNameFontFamily(),
            fontSize = 20.sp
        )
    }
}