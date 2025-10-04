package com.ma7moud3ly.quran.platform

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.model.AppVideo
import com.ma7moud3ly.quran.model.TvBackground
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.video10_thumbnail
import quran.composeapp.generated.resources.video11_thumbnail
import quran.composeapp.generated.resources.video12_thumbnail
import quran.composeapp.generated.resources.video13_thumbnail
import quran.composeapp.generated.resources.video14_thumbnail
import quran.composeapp.generated.resources.video15_thumbnail
import quran.composeapp.generated.resources.video16_thumbnail
import quran.composeapp.generated.resources.video17_thumbnail
import quran.composeapp.generated.resources.video18_thumbnail
import quran.composeapp.generated.resources.video19_thumbnail
import quran.composeapp.generated.resources.video1_thumbnail
import quran.composeapp.generated.resources.video20_thumbnail
import quran.composeapp.generated.resources.video21_thumbnail
import quran.composeapp.generated.resources.video22_thumbnail
import quran.composeapp.generated.resources.video23_thumbnail
import quran.composeapp.generated.resources.video24_thumbnail
import quran.composeapp.generated.resources.video2_thumbnail
import quran.composeapp.generated.resources.video3_thumbnail
import quran.composeapp.generated.resources.video4_thumbnail
import quran.composeapp.generated.resources.video5_thumbnail
import quran.composeapp.generated.resources.video6_thumbnail
import quran.composeapp.generated.resources.video7_thumbnail
import quran.composeapp.generated.resources.video8_thumbnail
import quran.composeapp.generated.resources.video9_thumbnail

class AndroidBackgroundVideos : BackgroundVideos {
    override val backgrounds: List<TvBackground> = androidSlides
}

actual fun getPlaybackVideos(): BackgroundVideos = AndroidBackgroundVideos()

private val androidSlides = listOf(
    TvBackground(
        id = "video1",
        video = AppVideo(
            name = "video1.mp4",
            thumbnail = Res.drawable.video1_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video2",
        video = AppVideo(
            name = "video2.mp4",
            thumbnail = Res.drawable.video2_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center
    ),
    TvBackground(
        id = "video3",
        video = AppVideo(
            name = "video3.mp4",
            thumbnail = Res.drawable.video3_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video4",
        video = AppVideo(
            name = "video4.mp4",
            thumbnail = Res.drawable.video4_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        color = Color.White,
        alignment = Alignment.Center
    ),
    TvBackground(
        id = "video5",
        video = AppVideo(
            name = "video5.mp4",
            thumbnail = Res.drawable.video5_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center
    ),
    TvBackground(
        id = "video6",
        video = AppVideo(
            name = "video6.mp4",
            thumbnail = Res.drawable.video6_thumbnail
        ),
        background = Color.White.copy(alpha = 0.3f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video7",
        video = AppVideo(
            name = "video7.mp4",
            thumbnail = Res.drawable.video7_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingTop = 150.dp
    ),
    TvBackground(
        id = "video8",
        video = AppVideo(
            name = "video8.mp4",
            thumbnail = Res.drawable.video8_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video9",
        video = AppVideo(
            name = "video9.mp4",
            thumbnail = Res.drawable.video9_thumbnail
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video10",
        video = AppVideo(
            name = "video10.mp4",
            thumbnail = Res.drawable.video10_thumbnail
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video11",
        video = AppVideo(
            name = "video11.mp4",
            thumbnail = Res.drawable.video11_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center,
        paddingBottom = 150.dp
    ),
    TvBackground(
        id = "video12",
        video = AppVideo(
            name = "video12.mp4",
            thumbnail = Res.drawable.video12_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center,
        paddingBottom = 200.dp
    ),
    TvBackground(
        id = "video13",
        video = AppVideo(
            name = "video13.mp4",
            thumbnail = Res.drawable.video13_thumbnail
        ),
        background = Color.White.copy(alpha = 0.05f),
        alignment = Alignment.TopCenter,
        paddingTop = 120.dp
    ),
    TvBackground(
        id = "video14",
        video = AppVideo(
            name = "video14.mp4",
            thumbnail = Res.drawable.video14_thumbnail
        ),
        background = Color.White.copy(alpha = 0.05f),
        alignment = Alignment.TopCenter,
        paddingTop = 120.dp,
    ),
    TvBackground(
        id = "video15",
        video = AppVideo(
            name = "video15.mp4",
            thumbnail = Res.drawable.video15_thumbnail
        ),
        background = Color.White.copy(alpha = 0.08f),
        alignment = Alignment.TopCenter,
        paddingTop = 150.dp
    ),
    TvBackground(
        id = "video16",
        video = AppVideo(
            name = "video16.mp4",
            thumbnail = Res.drawable.video16_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        color = Color.White,
        alignment = Alignment.TopCenter,
        paddingTop = 150.dp
    ),
    TvBackground(
        id = "video17",
        video = AppVideo(
            name = "video17.mp4",
            thumbnail = Res.drawable.video17_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video18",
        video = AppVideo(
            name = "video18.mp4",
            thumbnail = Res.drawable.video18_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.Center,
        paddingBottom = 150.dp
    ),
    TvBackground(
        id = "video19",
        video = AppVideo(
            name = "video19.mp4",
            thumbnail = Res.drawable.video19_thumbnail
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.Center
    ),
    TvBackground(
        id = "video20",
        video = AppVideo(
            name = "video20.mp4",
            thumbnail = Res.drawable.video20_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video21",
        video = AppVideo(
            name = "video21.mp4",
            thumbnail = Res.drawable.video21_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video22",
        video = AppVideo(
            name = "video22.mp4",
            thumbnail = Res.drawable.video22_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video23",
        video = AppVideo(
            name = "video23.mp4",
            thumbnail = Res.drawable.video23_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvBackground(
        id = "video24",
        video = AppVideo(
            name = "video24.mp4",
            thumbnail = Res.drawable.video24_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.TopCenter,
        paddingTop = 110.dp
    )
)