package com.ma7moud3ly.quran.platform

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.model.AppVideo
import com.ma7moud3ly.quran.model.TvBackground
import com.ma7moud3ly.quran.resources.Res
import com.ma7moud3ly.quran.resources.video11_thumbnail
import com.ma7moud3ly.quran.resources.video12_thumbnail
import com.ma7moud3ly.quran.resources.video13_thumbnail
import com.ma7moud3ly.quran.resources.video14_thumbnail
import com.ma7moud3ly.quran.resources.video15_thumbnail
import com.ma7moud3ly.quran.resources.video1_thumbnail
import com.ma7moud3ly.quran.resources.video3_thumbnail
import com.ma7moud3ly.quran.resources.video4_thumbnail
import com.ma7moud3ly.quran.resources.video5_thumbnail
import com.ma7moud3ly.quran.resources.video6_thumbnail
import com.ma7moud3ly.quran.resources.video7_thumbnail
import com.ma7moud3ly.quran.resources.video9_thumbnail

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
    )
)