package com.ma7moud3ly.quran.platform

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.model.AppVideo
import com.ma7moud3ly.quran.model.TvBackground
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.video10_thumbnail_ios
import quran.composeapp.generated.resources.video1_thumbnail_ios
import quran.composeapp.generated.resources.video2_thumbnail_ios
import quran.composeapp.generated.resources.video3_thumbnail_ios
import quran.composeapp.generated.resources.video4_thumbnail_ios
import quran.composeapp.generated.resources.video5_thumbnail_ios
import quran.composeapp.generated.resources.video6_thumbnail_ios
import quran.composeapp.generated.resources.video7_thumbnail_ios
import quran.composeapp.generated.resources.video8_thumbnail_ios
import quran.composeapp.generated.resources.video9_thumbnail_ios

class IosBackgroundVideos : BackgroundVideos {
    override val backgrounds: List<TvBackground> = iosSlides
}

actual fun getPlaybackVideos(): BackgroundVideos = IosBackgroundVideos()


private val iosSlides = listOf(
    TvBackground(
        id = "video1",
        video = AppVideo(
            name = "video1_ios.mp4",
            thumbnail = Res.drawable.video1_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video2",
        video = AppVideo(
            name = "video2_ios.mp4",
            thumbnail = Res.drawable.video2_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video3",
        video = AppVideo(
            name = "video3_ios.mp4",
            thumbnail = Res.drawable.video3_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video4",
        video = AppVideo(
            name = "video4_ios.mp4",
            thumbnail = Res.drawable.video4_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video5",
        video = AppVideo(
            name = "video5_ios.mp4",
            thumbnail = Res.drawable.video5_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video6",
        video = AppVideo(
            name = "video6_ios.mp4",
            thumbnail = Res.drawable.video6_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.3f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video7",
        video = AppVideo(
            name = "video7_ios.mp4",
            thumbnail = Res.drawable.video7_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video8",
        video = AppVideo(
            name = "video8_ios.mp4",
            thumbnail = Res.drawable.video8_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video9",
        video = AppVideo(
            name = "video9_ios.mp4",
            thumbnail = Res.drawable.video9_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video10",
        video = AppVideo(
            name = "video10_ios.mp4",
            thumbnail = Res.drawable.video10_thumbnail_ios
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    )
)