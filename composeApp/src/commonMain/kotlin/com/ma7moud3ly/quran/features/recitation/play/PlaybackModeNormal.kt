package com.ma7moud3ly.quran.features.recitation.play

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.reading.SuraName
import com.ma7moud3ly.quran.features.reading.VersesScrollbar
import com.ma7moud3ly.quran.features.reading.modes.SectionMultiVerses
import com.ma7moud3ly.quran.features.reading.modes.SectionSingleVerse
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.model.AppSettings
import com.ma7moud3ly.quran.model.VersesMode
import com.ma7moud3ly.quran.model.testMediaPlayerManager
import com.ma7moud3ly.quran.model.testMediaPlayerManagerInReelMode
import com.ma7moud3ly.quran.model.testRecitationSettings
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MyScreen
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.ui.arabicIslamicFontFamily
import com.ma7moud3ly.quran.ui.isCompactDevice
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.back
import quran.composeapp.generated.resources.copied
import quran.composeapp.generated.resources.forward
import quran.composeapp.generated.resources.pause
import quran.composeapp.generated.resources.play
import quran.composeapp.generated.resources.reciter_name
import quran.composeapp.generated.resources.settings
import quran.composeapp.generated.resources.tv


@Preview
@Composable
private fun NormalPlaybackPreview() {
    AppTheme {
        NormalPlayback(
            mediaPlayerManager = testMediaPlayerManager(),
            appSettings = { testRecitationSettings },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun NormalPlaybackPreviewLight() {
    AppTheme(darkTheme = false) {
        NormalPlayback(
            mediaPlayerManager = testMediaPlayerManager(),
            appSettings = { testRecitationSettings },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun NormalPlayback_ReelMode_Preview() {
    AppTheme {
        PlaybackScreenContent(enableReelMode = true) {
            NormalPlayback(
                mediaPlayerManager = testMediaPlayerManagerInReelMode(),
                appSettings = { testRecitationSettings },
                uiEvents = {}
            )
        }
    }
}

@Preview
@Composable
private fun NormalPlayback_ReelMode_PreviewLight() {
    AppTheme(darkTheme = false) {
        PlaybackScreenContent(enableReelMode = true) {
            NormalPlayback(
                mediaPlayerManager = testMediaPlayerManagerInReelMode(),
                appSettings = { testRecitationSettings },
                uiEvents = {}
            )
        }
    }
}


@OptIn(FlowPreview::class)
@Composable
fun NormalPlayback(
    mediaPlayerManager: MediaPlayerManager,
    appSettings: () -> AppSettings,
    uiEvents: (PlaybackEvents) -> Unit,
) {

    val settings = appSettings()
    val message = stringResource(Res.string.copied)
    val versesManager = remember(mediaPlayerManager) { mediaPlayerManager.getVerseManager() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun showCopiedMessage() {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    MyScreen(
        space = 0.dp,
        topBar = {
            if (mediaPlayerManager.isReelMode.not()) {
                Header(
                    mediaPlayer = mediaPlayerManager,
                    onSettings = { uiEvents(PlaybackEvents.OpenSettings) },
                    onBack = { uiEvents(PlaybackEvents.Back) }
                )
            } else HeaderInReelMode(
                mediaPlayer = mediaPlayerManager,
                onSettings = { uiEvents(PlaybackEvents.OpenSettings) },
                onBack = { uiEvents(PlaybackEvents.Back) }
            )
        },
        bottomBar = {
            VersesScrollbar(versesManager = versesManager)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        when (settings.versesMode) {
            VersesMode.Multiple -> {
                SectionMultiVerses(
                    chapter = mediaPlayerManager.getChapter(),
                    versesManager = versesManager,
                    font = settings.font,
                    showDivider = mediaPlayerManager.isReelMode.not(),
                    onCopyVerse = ::showCopiedMessage,
                )
            }

            VersesMode.Single -> {
                SectionSingleVerse(
                    versesManager = versesManager,
                    showNavigation = false,
                    font = settings.font,
                    onCopyVerse = ::showCopiedMessage,
                )
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    mediaPlayer: MediaPlayerManager,
    onSettings: () -> Unit,
    onBack: () -> Unit
) {
    val isPlaying by remember(mediaPlayer) { mediaPlayer.isPlaying }
    val reciter by remember { mediaPlayer.reciterState }
    val chapterName = mediaPlayer.chapterName

    Column {
        if (isCompactDevice()) MediumTopAppBar(
            modifier = Modifier.padding(vertical = 0.dp, horizontal = 8.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            navigationIcon = {
                SuraName(
                    chapterName = chapterName,
                    onClick = onBack,
                    iconSize = 24.dp,
                    fontSize = 24.sp
                )
            },
            title = {
                ReciterName(
                    reciterName = reciter.name,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(0.95f),
                    textModifier = Modifier.weight(1f)
                )
            }, actions = {
                MediaControllers(
                    isPlaying = isPlaying,
                    mediaPlayer = mediaPlayer,
                    onSettings = onSettings
                )
            }

        ) else TopAppBar(
            modifier = Modifier.padding(vertical = 0.dp, horizontal = 8.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            navigationIcon = {
                SuraName(
                    chapterName = chapterName,
                    onClick = onBack,
                    iconSize = 28.dp,
                    fontSize = 28.sp
                )
            },
            title = {
                ReciterName(
                    reciterName = reciter.name,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                )
            }, actions = {
                MediaControllers(
                    isPlaying = isPlaying,
                    mediaPlayer = mediaPlayer,
                    onSettings = onSettings
                )
            }

        )
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderInReelMode(
    mediaPlayer: MediaPlayerManager,
    onSettings: () -> Unit,
    onBack: () -> Unit
) {

    var showControls by remember { mutableStateOf(true) }
    val reciter by remember { mediaPlayer.reciterState }
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(5000)
            showControls = false
        }
    }

    Column {
        TopAppBar(
            modifier = Modifier.padding(vertical = 0.dp, horizontal = 8.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            navigationIcon = {
                SuraName(
                    chapterName = mediaPlayer.chapterName,
                    icon = null,
                    onClick = onBack,
                    iconSize = 25.dp,
                    fontSize = 25.sp
                )
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = reciter.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showControls = showControls.not() },
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    if (showControls) RoundButton(
                        icon = Res.drawable.settings,
                        iconSize = 24.dp,
                        onClick = onSettings
                    )
                }
            }
        )
        HorizontalDivider()
    }
}


@Composable
private fun ReciterName(
    reciterName: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    textModifier: Modifier = Modifier,
    startTvMode: (() -> Unit)? = null
) {
    MySurfaceRow(
        modifier = modifier,
        color = Color.Transparent,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        Text(
            text = "5",
            fontFamily = arabicIslamicFontFamily(),
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontSize = (fontSize.value + 5).sp,
        )
        BasicText(
            text = stringResource(
                Res.string.reciter_name,
                reciterName
            ),
            modifier = textModifier,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = fontSize,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = color,
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                maxFontSize = fontSize,
                minFontSize = 12.sp
            ),
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "6",
            fontFamily = arabicIslamicFontFamily(),
            color = color,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = (fontSize.value + 5).sp,
        )
        startTvMode?.let {
            IconButton(
                onClick = startTvMode
            ) {
                Icon(
                    painter = painterResource(Res.drawable.tv),
                    contentDescription = "",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun MediaControllers(
    isPlaying: Boolean,
    mediaPlayer: MediaPlayerManager,
    onSettings: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RoundButton(
            icon = Res.drawable.back,
            onClick = mediaPlayer::previous
        )
        if (isPlaying) RoundButton(
            icon = Res.drawable.pause,
            onClick = mediaPlayer::pause
        ) else RoundButton(
            icon = Res.drawable.play,
            onClick = mediaPlayer::resume
        )
        RoundButton(
            icon = Res.drawable.forward,
            onClick = mediaPlayer::next
        )
        RoundButton(
            icon = Res.drawable.settings,
            onClick = onSettings
        )
    }
}
