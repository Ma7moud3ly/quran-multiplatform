package com.ma7moud3ly.quran.features.recitation.play

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ma7moud3ly.quran.features.reading.SuraName
import com.ma7moud3ly.quran.features.reading.modes.formatVerse
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.model.PlaybackStates
import com.ma7moud3ly.quran.model.RecitationSettings
import com.ma7moud3ly.quran.model.TvSlide
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.model.testMediaPlayerManager
import com.ma7moud3ly.quran.model.testMediaPlayerManagerInReelMode
import com.ma7moud3ly.quran.model.testRecitationSettings
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.back
import quran.composeapp.generated.resources.close
import quran.composeapp.generated.resources.elgharib_noon_hafs
import quran.composeapp.generated.resources.forward
import quran.composeapp.generated.resources.pause
import quran.composeapp.generated.resources.play


@Preview
@Composable
private fun TvPlaybackPreview() {
    AppTheme {
        TvPlayback(
            slides = testSlidesManager.slides,
            mediaPlayerManager = testMediaPlayerManager(),
            appSettings = { testRecitationSettings },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun TvPlaybackPreviewLight() {
    AppTheme(darkTheme = false) {
        TvPlayback(
            slides = testSlidesManager.slides,
            mediaPlayerManager = testMediaPlayerManager(),
            appSettings = { testRecitationSettings },
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
                slides = testSlidesManager.slides,
                mediaPlayerManager = testMediaPlayerManagerInReelMode(),
                appSettings = { testRecitationSettings },
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
                slides = testSlidesManager.slides,
                mediaPlayerManager = testMediaPlayerManagerInReelMode(),
                appSettings = { testRecitationSettings },
                uiEvents = {}
            )
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun TvPlayback(
    slides: List<TvSlide>,
    mediaPlayerManager: MediaPlayerManager,
    appSettings: () -> RecitationSettings,
    uiEvents: (PlaybackEvents) -> Unit,
) {
    val settings = appSettings()
    val isPreview: Boolean = LocalInspectionMode.current
    val videoPlayerState = rememberVideoPlayerState()
    val selectedVerse by mediaPlayerManager.currentVerse.collectAsState(null)
    val isPlaying by remember { mediaPlayerManager.isPlaying }
    var showControls by remember { mutableStateOf(mediaPlayerManager.isReelMode.not()) }
    var slide by remember { mutableStateOf(slides[settings.tvSlide]) }
    val coroutineScope = rememberCoroutineScope()
    val slidesListState = rememberLazyListState()

    MyBackHandler {
        if (showControls.not()) showControls = true
        else uiEvents(PlaybackEvents.Back)
    }

    fun saveSlide(index: Int) {
        settings.tvSlide = index
        uiEvents(PlaybackEvents.SaveTvSlide(index))
    }

    fun nextSlide() {
        var index = settings.tvSlide
        if (index < slides.size - 1) index++
        else index = 0
        slide = slides[index]
        coroutineScope.launch {
            slidesListState.animateScrollToItem(index)
        }
        saveSlide(index)
    }

    fun previousSlide() {
        var index = settings.tvSlide
        if (index > 0) index--
        else index = slides.size - 1
        slide = slides[index]
        coroutineScope.launch {
            slidesListState.animateScrollToItem(index)
        }
        saveSlide(index)
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            PlaybackStates(showControls, slide, isPlaying, selectedVerse)
        }.debounce(5000).collect { (currentShowControls, _, _) ->
            if (currentShowControls) {
                showControls = false
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
                .clickable(
                    onClick = { showControls = showControls.not() },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            onSwipeRight = mediaPlayerManager::next,
            onSwipeLeft = mediaPlayerManager::previous,
            onSwipeUp = ::previousSlide,
            onSwipeDown = ::nextSlide
        ) {
            Header(
                mediaPlayer = mediaPlayerManager,
                modifier = Modifier
                    .fillMaxWidth()
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
                showControls = { showControls },
                onBack = { uiEvents(PlaybackEvents.Back) }
            )

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

            if (selectedVerse != null) Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = slide.paddingTop,
                        bottom = slide.paddingBottom
                    )
                    .align(slide.alignment)
            ) {
                ItemVerse(
                    verse = { selectedVerse!! },
                    background = if (slide.showVerseBackground)
                        slide.background
                    else Color.Transparent,
                    color = slide.color
                )
            }

            if (showControls) {
                SectionSlides(
                    slides = slides,
                    listState = slidesListState,
                    selectedSlide = { slide },
                    onSelect = {
                        slide = it
                        showControls = true
                        saveSlide(slides.indexOf(it))
                    },
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
    showControls: () -> Boolean,
    slide: () -> TvSlide,
    onBack: () -> Unit
) {
    val background = Color.White.copy(alpha = 0.3f)
    val color = slide().color
    val isPlaying by remember(mediaPlayer) { mediaPlayer.isPlaying }
    val isDownloading by remember { mediaPlayer.isDownloadingVerse }
    val reciter by remember { mediaPlayer.reciterState }

    val showControls = showControls()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SuraName(
            chapterName = mediaPlayer.chapterName,
            background = if (showControls) background
            else Color.Transparent,
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
        if (showControls.not()) {
            Text(
                text = reciter.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                color = color,
            )
        } else {
            RoundButton(
                icon = Res.drawable.back,
                onClick = mediaPlayer::previous,
                color = color,
                background = background,
            )
            if (isPlaying) RoundButton(
                icon = Res.drawable.pause,
                onClick = mediaPlayer::pause,
                color = color,
                background = background,
            ) else RoundButton(
                icon = Res.drawable.play,
                onClick = mediaPlayer::resume,
                color = color,
                background = background,
            )
            RoundButton(
                icon = Res.drawable.forward,
                onClick = mediaPlayer::next,
                color = color,
                background = background,
            )
            Spacer(Modifier.width(8.dp))
            RoundButton(
                icon = Res.drawable.close,
                background = background,
                color = color,
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
    Surface(
        color = background,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = formatVerse(verse()),
            textAlign = TextAlign.Justify,
            fontFamily = FontFamily(Font(Res.font.elgharib_noon_hafs)),
            fontSize = 22.sp,
            lineHeight = 40.sp,
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

