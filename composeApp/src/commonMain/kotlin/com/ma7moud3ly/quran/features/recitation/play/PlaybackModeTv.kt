package com.ma7moud3ly.quran.features.recitation.play

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ma7moud3ly.quran.features.reading.SuraName
import com.ma7moud3ly.quran.features.reading.modes.formatVerse
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.managers.SlidesManager
import com.ma7moud3ly.quran.model.PlaybackStates
import com.ma7moud3ly.quran.model.SlideControls
import com.ma7moud3ly.quran.model.TvSlide
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.model.showControls
import com.ma7moud3ly.quran.model.showTitle
import com.ma7moud3ly.quran.model.showVerse
import com.ma7moud3ly.quran.model.testMediaPlayerManager
import com.ma7moud3ly.quran.model.testMediaPlayerManagerInReelMode
import com.ma7moud3ly.quran.model.testSlidesManager
import com.ma7moud3ly.quran.platform.MyBackHandler
import com.ma7moud3ly.quran.platform.ShowFullScreen
import com.ma7moud3ly.quran.platform.VideoPlayer
import com.ma7moud3ly.quran.platform.isMobile
import com.ma7moud3ly.quran.platform.rememberVideoPlayerState
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.LocalPlatform
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.ui.SwipeableBox
import com.ma7moud3ly.quran.ui.tvFontFamily
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.back
import quran.composeapp.generated.resources.close
import quran.composeapp.generated.resources.forward
import quran.composeapp.generated.resources.pause
import quran.composeapp.generated.resources.play


@Preview
@Composable
private fun TvPlaybackPreview() {
    AppTheme {
        TvPlayback(
            slidesManager = testSlidesManager,
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
            slidesManager = testSlidesManager,
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
                slidesManager = testSlidesManager,
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
                slidesManager = testSlidesManager,
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
    slidesManager: SlidesManager,
    uiEvents: (PlaybackEvents) -> Unit,
) {
    val isPreview: Boolean = LocalInspectionMode.current
    val videoPlayerState = rememberVideoPlayerState()
    val selectedVerse by mediaPlayerManager.currentVerseState.collectAsState(null)
    val isPlaying by remember { mediaPlayerManager.isPlaying }
    var slide by remember { slidesManager.selectedSlide }
    var controls by remember { slidesManager.slideControls }
    val coroutineScope = rememberCoroutineScope()
    val slidesListState = rememberLazyListState()

    MyBackHandler {
        if (controls.showControls.not()) {
            slidesManager.showControls()
        } else {
            uiEvents(PlaybackEvents.Back)
        }
    }

    LaunchedEffect(Unit) {
        if (mediaPlayerManager.isReelMode) slidesManager.toggleSlideControls()
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            PlaybackStates(controls, slide, isPlaying, selectedVerse)
        }.debounce(5000).collect { (controls, _, _) ->
            if (controls.showControls) {
                slidesManager.toggleSlideControls()
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
        modifier = Modifier.fillMaxWidth().background(Color.Red),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        SwipeableBox(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = slidesManager::toggleSlideControls,
                    onLongClick = slidesManager::showControls,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            onSwipeRight = {
                mediaPlayerManager.next()
                slidesManager.showVerse()
            },
            onSwipeLeft = {
                mediaPlayerManager.previous()
                slidesManager.showVerse()
            },
            onSwipeUp = {
                slidesManager.nextSlide {
                    coroutineScope.launch {
                        slidesListState.animateScrollToItem(it)
                    }
                }
            },
            onSwipeDown = {
                slidesManager.previousSlide {
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
                        top = if (mediaPlayerManager.isReelMode) 8.dp else 24.dp
                    ),
                slide = { slide },
                slideControls = { controls },
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

            if (isPreview) {
                Image(
                    painter = painterResource(slide.video.thumbnail),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillHeight
                )
            } else {
                VideoPlayer(
                    video = { slide.video },
                    state = videoPlayerState,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(slide.alignment)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = slide.paddingTop,
                        bottom = slide.paddingBottom
                    )
            ) {
                if (controls.showVerse && selectedVerse != null) {
                    ItemVerse(
                        verse = { selectedVerse!! },
                        background = if (slide.showVerseBackground)
                            slide.background
                        else Color.Transparent,
                        color = slide.color
                    )
                }
            }

            if (controls.showControls) {
                SectionSlides(
                    slides = slidesManager.slides,
                    listState = slidesListState,
                    selectedSlide = { slide },
                    onSelect = slidesManager::selectSlide,
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
    slideControls: () -> SlideControls,
    slide: () -> TvSlide,
    onBack: () -> Unit
) {
    val controls = slideControls()
    if (controls.showTitle.not()) return
    val background = Color.White.copy(alpha = 0.3f)
    val color = slide().color
    val isDownloading by remember { mediaPlayer.isDownloadingVerse }
    val reciter by remember { mediaPlayer.reciter }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SuraName(
            chapterName = mediaPlayer.chapterName,
            background = if (controls.showControls) background
            else Color.Transparent,
            fontSize = 24.sp,
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
            fontSize = 12.sp,
            color = color,
        )
        if (controls.showControls) {
            RoundButton(
                icon = Res.drawable.close,
                background = background,
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
    verse: () -> Verse,
    color: Color,
    background: Color,
    modifier: Modifier = Modifier
) {
    val platform = LocalPlatform.current
    Surface(
        color = background,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = formatVerse(verse()),
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
private fun SectionSlides(
    slides: List<TvSlide>,
    listState: LazyListState,
    selectedSlide: () -> TvSlide,
    modifier: Modifier,
    onSelect: (TvSlide) -> Unit
) {
    LazyRow(
        state = listState,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(slides) {
            ItemSlide(
                slide = it,
                selected = it == selectedSlide(),
                onClick = { onSelect(it) },
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
            )
        }
    }
}

@Composable
private fun ItemSlide(
    slide: TvSlide,
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
            painter = painterResource(slide.video.thumbnail),
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

