package com.ma7moud3ly.quran.features.recitation.play

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import com.ma7moud3ly.quran.features.reading.SuraName
import com.ma7moud3ly.quran.features.reading.modes.formatVerse
import com.ma7moud3ly.quran.managers.BackgroundsManager
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.PlaybackStates
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.TvBackground
import com.ma7moud3ly.quran.model.TvControls
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.model.showControls
import com.ma7moud3ly.quran.model.showHeader
import com.ma7moud3ly.quran.model.showReciter
import com.ma7moud3ly.quran.model.showVerseContent
import com.ma7moud3ly.quran.model.testBackgroundsManager
import com.ma7moud3ly.quran.model.testMediaPlayerManager
import com.ma7moud3ly.quran.model.testMediaPlayerManagerInReelMode
import com.ma7moud3ly.quran.platform.MyBackHandler
import com.ma7moud3ly.quran.platform.ShowFullScreen
import com.ma7moud3ly.quran.platform.VideoPlayer
import com.ma7moud3ly.quran.platform.isJvm
import com.ma7moud3ly.quran.platform.isMobile
import com.ma7moud3ly.quran.platform.rememberVideoPlayerState
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.LocalPlatform
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.ui.SwipeableBox
import com.ma7moud3ly.quran.ui.isCompactDevice
import com.ma7moud3ly.quran.ui.tvFontFamily
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.back
import quran.composeapp.generated.resources.close
import quran.composeapp.generated.resources.forward
import quran.composeapp.generated.resources.icon
import quran.composeapp.generated.resources.pause
import quran.composeapp.generated.resources.play


@Preview
@Composable
private fun TvPlaybackPreview() {
    AppTheme {
        TvPlayback(
            backgroundsManager = testBackgroundsManager,
            mediaPlayerManager = testMediaPlayerManager(),
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun TvPlaybackPreviewLight() {
    AppTheme(darkTheme = false) {
        TvPlayback(
            backgroundsManager = testBackgroundsManager,
            mediaPlayerManager = testMediaPlayerManager(),
            uiEvents = {}
        )
    }
}


@Preview
@Composable
private fun TvPlayback_ReelMode_Preview() {
    AppTheme(darkTheme = true) {
        PlaybackScreenContent(enableReelMode = true) {
            TvPlayback(
                backgroundsManager = testBackgroundsManager,
                mediaPlayerManager = testMediaPlayerManagerInReelMode(),
                uiEvents = {}
            )
        }
    }
}

@Preview
@Composable
private fun TvPlayback_ReelMode_PreviewLight() {
    AppTheme(darkTheme = false) {
        PlaybackScreenContent(enableReelMode = true) {
            TvPlayback(
                backgroundsManager = testBackgroundsManager,
                mediaPlayerManager = testMediaPlayerManagerInReelMode(),
                uiEvents = {}
            )
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun TvPlayback(
    mediaPlayerManager: MediaPlayerManager,
    backgroundsManager: BackgroundsManager,
    uiEvents: (PlaybackEvents) -> Unit,
) {
    val isPreview: Boolean = LocalInspectionMode.current
    val videoPlayerState = rememberVideoPlayerState()
    val selectedVerse by mediaPlayerManager.currentVerseState.collectAsState(null)
    val isPlaying by remember { mediaPlayerManager.isPlaying }
    val background by remember { backgroundsManager.selectedBackground }
    var controls by remember { backgroundsManager.tvControls }
    val coroutineScope = rememberCoroutineScope()
    val slidesListState = rememberLazyListState()

    MyBackHandler {
        if (controls.showControls.not()) {
            backgroundsManager.showControls()
        } else {
            uiEvents(PlaybackEvents.Back)
        }
    }

    LaunchedEffect(Unit) {
        if (mediaPlayerManager.isReelMode) {
            if (mediaPlayerManager.singleReciter()) {
                backgroundsManager.showReciter()
                delay(5000)
                backgroundsManager.showVerseContent()
            } else {
                backgroundsManager.showVerseContent()
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            PlaybackStates(controls, background, isPlaying, selectedVerse)
        }.debounce(5000).collect { (controls, _, _) ->
            if (controls.showControls) {
                backgroundsManager.toggleBackgroundControls()
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { isPlaying }.collect {
            if (isPlaying) videoPlayerState.play()
            else videoPlayerState.pause()
        }
    }


    if (isPreview.not() && mediaPlayerManager.isReelMode.not()) {
        ShowFullScreen()
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        SwipeableBox(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = backgroundsManager::toggleBackgroundControls,
                    onLongClick = backgroundsManager::showControls,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            onSwipeRight = {
                mediaPlayerManager.next()
                if (backgroundsManager.hideAllControls) {
                    backgroundsManager.showVerseContent()
                }
            },
            onSwipeLeft = {
                mediaPlayerManager.previous()
                if (backgroundsManager.hideAllControls) {
                    backgroundsManager.showVerseContent()
                }
            },
            onSwipeUp = {
                backgroundsManager.nextBackground {
                    coroutineScope.launch {
                        slidesListState.animateScrollToItem(it)
                    }
                }
            },
            onSwipeDown = {
                backgroundsManager.previousBackground {
                    coroutineScope.launch {
                        slidesListState.animateScrollToItem(it)
                    }
                }
            }
        ) {
            Header(
                mediaPlayer = mediaPlayerManager,
                modifier = Modifier.fillMaxWidth()
                    .zIndex(2f)
                    .statusBarsPadding()
                    .align(Alignment.TopCenter)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 36.dp,
                        top = 16.dp
                    ),
                tvBackground = { background },
                tvControls = { controls },
                onBack = { uiEvents(PlaybackEvents.Back) }
            )

            if (controls.showControls) {
                SectionControls(
                    mediaPlayer = mediaPlayerManager,
                    iconSize = 32.dp,
                    iconPadding = 6.dp,
                    modifier = Modifier
                        .zIndex(2f)
                        .align(Alignment.Center)
                )
            }


            if (controls.showReciter) {
                RecitationDetails(
                    reciter = { mediaPlayerManager.reciter.value },
                    background = background,
                    chapter = mediaPlayerManager.getChapter(),
                    modifier = Modifier
                        .zIndex(2f)
                        .fillMaxWidth(if (isCompactDevice()) 1f else 0.6f)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                )
            }

            if (isPreview) {
                Image(
                    painter = background.video.getPainter(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillHeight
                )
            } else {
                VideoPlayer(
                    video = { background.video },
                    state = videoPlayerState,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(background.alignment)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = background.paddingTop,
                        bottom = background.paddingBottom
                    )
            ) {
                if (controls.showVerseContent) {
                    ItemVerse(
                        verse = { selectedVerse },
                        background = background.background,
                        color = background.color
                    )
                }
            }

            if (controls.showControls) {
                SectionBackgrounds(
                    backgrounds = backgroundsManager.backgrounds,
                    listState = slidesListState,
                    selectedBackground = { background },
                    onSelect = backgroundsManager::selectBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(
                            top = 48.dp,
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                )
            }
        }
    }
}


@Composable
private fun Header(
    modifier: Modifier,
    mediaPlayer: MediaPlayerManager,
    tvControls: () -> TvControls,
    tvBackground: () -> TvBackground,
    onBack: () -> Unit
) {
    val controls = tvControls()
    if (controls.showHeader.not()) return
    val backgroundColor = Color.White.copy(alpha = 0.3f)
    val color = tvBackground().color
    val isDownloading by remember { mediaPlayer.isDownloadingVerse }
    val reciter by remember { mediaPlayer.reciter }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SuraName(
            chapterName = mediaPlayer.chapterName,
            background = if (controls.showControls) backgroundColor
            else Color.Transparent,
            fontSize = 20.sp,
            color = color,
            icon = null,
            onClick = onBack
        )
        Spacer(Modifier.weight(1f))
        if (isDownloading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = reciter.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp,
            color = color,
        )
        if (controls.showControls) {
            RoundButton(
                icon = Res.drawable.close,
                background = backgroundColor,
                color = color,
                iconSize = 22.dp,
                iconPadding = 4.dp,
                onClick = onBack
            )
        }
    }
}


@Composable
private fun ItemVerse(
    verse: () -> Verse?,
    color: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    val verse = verse() ?: return
    val platform = LocalPlatform.current
    Surface(
        color = background,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = formatVerse(verse),
            textAlign = TextAlign.Justify,
            fontFamily = tvFontFamily(),
            fontSize = if (platform.isMobile) 16.sp else 20.sp,
            lineHeight = 2.em,
            color = color,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp
                )
        )
    }
}

@Composable
private fun SectionBackgrounds(
    backgrounds: List<TvBackground>,
    listState: LazyListState,
    selectedBackground: () -> TvBackground,
    modifier: Modifier,
    onSelect: (TvBackground) -> Unit
) {
    LazyRow(
        state = listState,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(backgrounds) {
            ItemBackground(
                background = it,
                selected = it == selectedBackground(),
                onClick = { onSelect(it) },
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
            )
        }
    }
}

@Composable
private fun ItemBackground(
    background: TvBackground,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val platform = LocalPlatform.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.secondary
            else Color.White
        )
    ) {
        Image(
            painter = background.video.getPainter(),
            contentDescription = null,
            modifier = modifier,
            contentScale = if (platform.isMobile) ContentScale.FillWidth
            else ContentScale.Crop
        )
    }
}

@Composable
private fun SectionControls(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayerManager,
    iconSize: Dp = 26.dp,
    iconPadding: Dp = 6.dp
) {
    val isPlaying by remember(mediaPlayer) { mediaPlayer.isPlaying }
    val background = Color.White.copy(alpha = 0.3f)
    val color = MaterialTheme.colorScheme.onPrimary

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RoundButton(
            icon = Res.drawable.back,
            onClick = mediaPlayer::previous,
            color = color,
            iconSize = iconSize,
            iconPadding = iconPadding,
            background = background,
        )
        if (isPlaying) RoundButton(
            icon = Res.drawable.pause,
            onClick = mediaPlayer::pause,
            color = color,
            iconSize = iconSize,
            iconPadding = iconPadding,
            background = background,
        ) else RoundButton(
            icon = Res.drawable.play,
            onClick = mediaPlayer::resume,
            color = color,
            iconSize = iconSize,
            iconPadding = iconPadding,
            background = background,
        )
        RoundButton(
            icon = Res.drawable.forward,
            onClick = mediaPlayer::next,
            color = color,
            iconSize = iconSize,
            iconPadding = iconPadding,
            background = background,
        )
    }
}

@Composable
private fun RecitationDetails(
    modifier: Modifier = Modifier,
    reciter: () -> Reciter,
    background: TvBackground,
    chapter: Chapter
) {
    val reciter = reciter()
    val color = background.color
    val platform = LocalPlatform.current
    MySurfaceRow(
        surfaceModifier = modifier,
        color = background.background,
        cornerRadius = if (isCompactDevice()) 0.dp else 8.dp,
        modifier = Modifier.padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReciterImage(
            imageUrl = reciter.imageUrl,
            placeholder = Res.drawable.icon,
            borderWidth = 3.dp,
            size = 70.dp
        )
        Column {
            Text(
                text = reciter.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = color,
                maxLines = 1,
                fontSize = 13.sp,
            )
            if (platform.isJvm) Spacer(Modifier.height(8.dp))
            Text(
                text = "سورة ${chapter.name}",
                style = MaterialTheme.typography.bodySmall,
                color = color,
                maxLines = 1,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
fun ReciterImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    placeholder: DrawableResource,
    size: Dp = 100.dp,
    borderWidth: Dp = 6.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "borderRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(size + borderWidth * 2),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size + borderWidth * 2)
                .graphicsLayer { rotationZ = rotation }
                .border(
                    width = borderWidth,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF),
                            Color(0xFF3CA113),
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF)
                        )
                    ),
                    shape = CircleShape
                )
        )

        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                placeholder = painterResource(placeholder),
                error = painterResource(placeholder)
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        )
    }
}

