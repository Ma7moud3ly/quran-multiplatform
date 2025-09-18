package com.ma7moud3ly.quran.features.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.reading.modes.SectionContinuesVerses
import com.ma7moud3ly.quran.features.reading.modes.SectionMultiVerses
import com.ma7moud3ly.quran.features.reading.modes.SectionSingleVerse
import com.ma7moud3ly.quran.managers.VersesManager
import com.ma7moud3ly.quran.model.AppSettings
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.VersesMode
import com.ma7moud3ly.quran.model.isContinues
import com.ma7moud3ly.quran.model.testChapter
import com.ma7moud3ly.quran.model.testReadingSettings
import com.ma7moud3ly.quran.model.testVersesManager
import com.ma7moud3ly.quran.platform.MyBackHandler
import com.ma7moud3ly.quran.platform.ShowFullScreen
import com.ma7moud3ly.quran.platform.isMobile
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.LocalPlatform
import com.ma7moud3ly.quran.ui.MyScreen
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.ui.suraNameFontFamily
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.audio_file
import quran.composeapp.generated.resources.copied
import quran.composeapp.generated.resources.fullscreen
import quran.composeapp.generated.resources.fullscreen_exit
import quran.composeapp.generated.resources.menu
import quran.composeapp.generated.resources.settings


@Preview
@Composable
private fun ReadingScreenPreview() {
    AppTheme {
        ReadingScreenContent(
            chapter = testChapter,
            versesManager = testVersesManager,
            appSettings = { testReadingSettings },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ReadingScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        ReadingScreenContent(
            chapter = testChapter,
            versesManager = testVersesManager,
            appSettings = { testReadingSettings },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ReadingScreenFullPreview() {
    AppTheme {
        ReadingScreenContent(
            chapter = testChapter,
            versesManager = testVersesManager,
            showFullScreen = true,
            appSettings = { testReadingSettings },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun ReadingScreenFullPreviewLight() {
    AppTheme(darkTheme = false) {
        ReadingScreenContent(
            chapter = testChapter,
            versesManager = testVersesManager,
            showFullScreen = true,
            appSettings = { testReadingSettings },
            uiEvents = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreenContent(
    chapter: Chapter,
    showFullScreen: Boolean = false,
    versesManager: VersesManager,
    appSettings: () -> AppSettings,
    uiEvents: (ReadingEvents) -> Unit,
) {

    val settings = appSettings()
    var showFullScreen by rememberSaveable { mutableStateOf(showFullScreen) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    MyBackHandler {
        if (showFullScreen) showFullScreen = false
        else uiEvents(ReadingEvents.Back)
    }

    if (showFullScreen) {
        ShowFullScreen()
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val message = stringResource(Res.string.copied)
    fun showCopiedMessage() {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    MyScreen(
        space = 0.dp,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (showFullScreen.not()) {
                Header(
                    scrollBehavior = scrollBehavior,
                    isContinueMode = settings.versesMode.isContinues,
                    chapterName = chapter.chapterFullName(),
                    fullScreen = { showFullScreen },
                    onToggleScreen = { showFullScreen = showFullScreen.not() },
                    onOpenSettings = { uiEvents(ReadingEvents.OpenSettings) },
                    onBack = { uiEvents(ReadingEvents.Back) },
                    onPlay = {
                        val verseId = versesManager.selectedVerseId
                        uiEvents(ReadingEvents.PlayVerse(verseId))
                    }
                )
            }
        },
        bottomBar = {
            if (settings.versesMode.isContinues.not()) {
                VersesScrollbar(versesManager = versesManager)
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        when (settings.versesMode) {
            VersesMode.Continues -> {
                SectionContinuesVerses(
                    versesManager = versesManager,
                    chapter = chapter,
                    font = settings.font,
                    onClick = { /*showFullScreen = showFullScreen.not()*/ },
                    onNextChapter = { uiEvents(ReadingEvents.NextChapter) },
                    onPreviousChapter = { uiEvents(ReadingEvents.PreviousChapter) },
                )
            }

            VersesMode.Multiple -> {
                SectionMultiVerses(
                    chapter = chapter,
                    font = settings.font,
                    versesManager = versesManager,
                    onCopyVerse = ::showCopiedMessage,
                    onNextChapter = { uiEvents(ReadingEvents.NextChapter) },
                    onPreviousChapter = { uiEvents(ReadingEvents.PreviousChapter) },
                )
            }

            VersesMode.Single -> {
                SectionSingleVerse(
                    versesManager = versesManager,
                    font = settings.font,
                    onCopyVerse = ::showCopiedMessage,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    scrollBehavior: TopAppBarScrollBehavior,
    isContinueMode: Boolean,
    chapterName: String,
    fullScreen: () -> Boolean,
    onToggleScreen: () -> Unit,
    onOpenSettings: () -> Unit,
    onPlay: () -> Unit,
    onBack: () -> Unit
) {
    val isVisible by remember { derivedStateOf { scrollBehavior.state.collapsedFraction < 1 } }
    val platform = LocalPlatform.current

    Column {
        TopAppBar(
            scrollBehavior = scrollBehavior,
            modifier = Modifier.padding(vertical = 0.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
            ),
            title = {
                SuraName(
                    chapterName = chapterName,
                    onClick = onBack,
                    iconSize = 24.dp,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }, actions = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    if (isContinueMode.not()) RoundButton(
                        icon = Res.drawable.audio_file,
                        onClick = onPlay
                    )
                    RoundButton(
                        icon = Res.drawable.settings,
                        onClick = onOpenSettings
                    )
                    if (isContinueMode && platform.isMobile) {
                        if (fullScreen()) RoundButton(
                            icon = Res.drawable.fullscreen_exit,
                            onClick = onToggleScreen
                        ) else RoundButton(
                            icon = Res.drawable.fullscreen,
                            onClick = onToggleScreen
                        )
                    }
                }
            }
        )
        if (isVisible) HorizontalDivider()
    }
}

@Composable
internal fun SuraName(
    chapterName: String,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    iconSize: Dp = 24.dp,
    icon: DrawableResource? = Res.drawable.menu,
    fontSize: TextUnit = 24.sp,
    onClick: (() -> Unit)? = null
) {
    MySurfaceRow(
        shape = RoundedCornerShape(24.dp),
        space = 8.dp,
        color = background,
        modifier = modifier,
        onClick = onClick
    ) {
        icon?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = "",
                tint = color,
                modifier = Modifier.size(iconSize)
            )
        }
        Text(
            text = chapterName,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = suraNameFontFamily(),
            fontSize = fontSize,
            color = color,
        )
    }
}
