package com.ma7moud3ly.quran.features.recitation.play

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.model.showControls
import com.ma7moud3ly.quran.model.showReciter
import com.ma7moud3ly.quran.model.showVerse
import com.ma7moud3ly.quran.model.showVerseAndHeader
import com.ma7moud3ly.quran.model.showVerseAndReciter
import com.ma7moud3ly.quran.model.testBackgroundsManager
import com.ma7moud3ly.quran.model.testMediaPlayerManager
import com.ma7moud3ly.quran.model.testMediaPlayerManagerInReelMode
import com.ma7moud3ly.quran.platform.MyBackHandler
import com.ma7moud3ly.quran.platform.ShowFullScreen
import com.ma7moud3ly.quran.platform.VideoPlayer
import com.ma7moud3ly.quran.platform.isMobile
import com.ma7moud3ly.quran.platform.rememberVideoPlayerState
import com.ma7moud3ly.quran.resources.Res
import com.ma7moud3ly.quran.resources.back
import com.ma7moud3ly.quran.resources.close
import com.ma7moud3ly.quran.resources.forward
import com.ma7moud3ly.quran.resources.icon
import com.ma7moud3ly.quran.resources.pause
import com.ma7moud3ly.quran.resources.play
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.LocalPlatform
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
            if (controls.showControls ||
                controls.showVerseAndHeader
            ) Header(
                mediaPlayer = mediaPlayerManager,
                modifier = Modifier.fillMaxWidth()
                    .statusBarsPadding()
                    .zIndex(2f)
                    .align(Alignment.TopCenter)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 36.dp,
                        top = 16.dp
                    ),
                tvBackground = { background },
                showControls = controls.showControls,
                onBack = { uiEvents(PlaybackEvents.Back) }
            )

            // Show Controls
            if (controls.showControls) {
                SectionControls(
                    mediaPlayer = mediaPlayerManager,
                    iconSize = 32.dp,
                    iconPadding = 6.dp,
                    modifier = Modifier
                        .zIndex(2f)
                        .align(Alignment.Center)
                )

                SectionBackgrounds(
                    backgrounds = backgroundsManager.backgrounds,
                    listState = slidesListState,
                    selectedBackground = { background },
                    onSelect = backgroundsManager::selectBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .zIndex(2f)
                        .padding(
                            top = 48.dp,
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                )
            }

            // Show Reciter view
            if (controls.showReciter) {
                RecitationDetails(
                    reciter = { mediaPlayerManager.reciter.value },
                    background = background,
                    chapter = mediaPlayerManager.getChapter(),
                    isPlaying = { isPlaying },
                    modifier = Modifier
                        .zIndex(2f)
                        .fillMaxWidth(if (isCompactDevice()) 1f else 0.6f)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                )
            }

            if (controls.showVerseAndHeader ||
                controls.showVerse
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .zIndex(3f)
                        .align(background.alignment)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = background.paddingTop,
                            bottom = background.paddingBottom
                        )
                ) {
                    ItemVerse(
                        verse = { selectedVerse },
                        background = background.background,
                        color = background.color
                    )
                }
            }

            if (controls.showVerseAndReciter) {
                Column(
                    modifier = Modifier.zIndex(2f)
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.weight(0.4f),
                        contentAlignment = Alignment.Center
                    ) {
                        SuraName(
                            chapterName = mediaPlayerManager.chapterName,
                            background = Color.Transparent,
                            fontSize = 32.sp,
                            color = Color.White,
                            icon = null
                        )
                    }
                    Box(Modifier.weight(0.6f)) {
                        ItemVerse(
                            verse = { selectedVerse },
                            fontSize = if (LocalPlatform.current.isMobile) 18.sp else 22.sp,
                            background = Color.Transparent,
                            color = Color.White
                        )
                    }
                }
            }

            /// Video Player
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
        }
    }
}


@Composable
private fun Header(
    modifier: Modifier,
    mediaPlayer: MediaPlayerManager,
    showControls: Boolean,
    tvBackground: () -> TvBackground,
    onBack: () -> Unit
) {
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
            background = if (showControls) backgroundColor
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
        if (showControls) {
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
    modifier: Modifier = Modifier,
    fontSize: TextUnit = if (LocalPlatform.current.isMobile) 16.sp else 20.sp,
) {
    val verse = verse() ?: return
    Surface(
        color = background,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = formatVerse(verse),
            textAlign = TextAlign.Justify,
            fontFamily = tvFontFamily(),
            fontSize = fontSize,
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
    isPlaying: () -> Boolean,
    reciter: () -> Reciter,
    background: TvBackground,
    chapter: Chapter
) {
    val reciter = reciter()
    val color = Color.White

    Row(
        modifier = modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background.background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReciterImage(
            imageUrl = reciter.imageUrl,
            placeholder = Res.drawable.icon,
            borderWidth = 3.dp,
            size = 66.dp,
            animate = isPlaying()
        )

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = reciter.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                maxLines = 1,
                fontSize = 13.sp,
            )
            Text(
                text = "سورة ${chapter.name}",
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.65f),
                maxLines = 1,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun ReciterImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    placeholder: DrawableResource,
    size: Dp = 100.dp,
    borderWidth: Dp = 3.dp,
    animate: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rings")

    val rotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animate) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerRing"
    )

    val rotationInner by infiniteTransition.animateFloat(
        initialValue = if (animate) 360f else 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerRing"
    )

    val gap = 4.dp
    val ringSpacing = 5.dp
    val innerRingSize = size + (gap + borderWidth) * 2
    val outerRingSize = innerRingSize + (ringSpacing + borderWidth) * 2

    Box(
        modifier = modifier.size(outerRingSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(outerRingSize)
                .graphicsLayer { rotationZ = rotationOuter }
        ) {
            val strokePx = borderWidth.toPx()
            val radius = (outerRingSize.toPx() - strokePx) / 2f
            val sweepAngle = 60f
            val gapAngle = 30f
            var startAngle = 0f
            while (startAngle < 360f) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(Color(0xFF3CA113), Color(0xFF7ED957))),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle + gapAngle
            }
        }

        Canvas(
            modifier = Modifier
                .size(innerRingSize)
                .graphicsLayer { rotationZ = rotationInner }
        ) {
            val strokePx = borderWidth.toPx()
            val radius = (innerRingSize.toPx() - strokePx) / 2f
            val sweepAngle = 100f
            val gapAngle = 20f
            var startAngle = 0f
            while (startAngle < 360f) {
                drawArc(
                    color = Color(0xFF3CA113).copy(alpha = 0.5f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle + gapAngle
            }
        }

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