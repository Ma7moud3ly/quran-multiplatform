package com.ma7moud3ly.quran.managers

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.model.MyVideo
import com.ma7moud3ly.quran.model.TvSlide
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

class AndroidSlidesManager : SlidesManager {
    override val slides: List<TvSlide> = androidSlides
}

actual fun getSlidesManager(): SlidesManager = AndroidSlidesManager()

private val androidSlides = listOf(
    TvSlide(
        id = "video1",
        video = MyVideo(
            name = "video1.mp4",
            thumbnail = Res.drawable.video1_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video2",
        video = MyVideo(
            name = "video2.mp4",
            thumbnail = Res.drawable.video2_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center
    ),
    TvSlide(
        id = "video3",
        video = MyVideo(
            name = "video3.mp4",
            thumbnail = Res.drawable.video3_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video4",
        video = MyVideo(
            name = "video4.mp4",
            thumbnail = Res.drawable.video4_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        color = Color.White,
        alignment = Alignment.Center
    ),
    TvSlide(
        id = "video5",
        video = MyVideo(
            name = "video5.mp4",
            thumbnail = Res.drawable.video5_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center
    ),
    TvSlide(
        id = "video6",
        video = MyVideo(
            name = "video6.mp4",
            thumbnail = Res.drawable.video6_thumbnail
        ),
        background = Color.White.copy(alpha = 0.3f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video7",
        video = MyVideo(
            name = "video7.mp4",
            thumbnail = Res.drawable.video7_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.Center,
        paddingTop = 150.dp
    ),
    TvSlide(
        id = "video8",
        video = MyVideo(
            name = "video8.mp4",
            thumbnail = Res.drawable.video8_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video9",
        video = MyVideo(
            name = "video9.mp4",
            thumbnail = Res.drawable.video9_thumbnail
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video10",
        video = MyVideo(
            name = "video10.mp4",
            thumbnail = Res.drawable.video10_thumbnail
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video11",
        video = MyVideo(
            name = "video11.mp4",
            thumbnail = Res.drawable.video11_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center,
        paddingBottom = 150.dp
    ),
    TvSlide(
        id = "video12",
        video = MyVideo(
            name = "video12.mp4",
            thumbnail = Res.drawable.video12_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        alignment = Alignment.Center,
        paddingBottom = 200.dp
    ),
    TvSlide(
        id = "video13",
        video = MyVideo(
            name = "video13.mp4",
            thumbnail = Res.drawable.video13_thumbnail
        ),
        background = Color.White.copy(alpha = 0.05f),
        alignment = Alignment.TopCenter,
        paddingTop = 120.dp
    ),
    TvSlide(
        id = "video14",
        video = MyVideo(
            name = "video14.mp4",
            thumbnail = Res.drawable.video14_thumbnail
        ),
        background = Color.White.copy(alpha = 0.05f),
        alignment = Alignment.TopCenter,
        paddingTop = 120.dp,
    ),
    TvSlide(
        id = "video15",
        video = MyVideo(
            name = "video15.mp4",
            thumbnail = Res.drawable.video15_thumbnail
        ),
        background = Color.White.copy(alpha = 0.08f),
        alignment = Alignment.TopCenter,
        paddingTop = 150.dp
    ),
    TvSlide(
        id = "video16",
        video = MyVideo(
            name = "video16.mp4",
            thumbnail = Res.drawable.video16_thumbnail
        ),
        background = Color.White.copy(alpha = 0.2f),
        color = Color.White,
        alignment = Alignment.TopCenter,
        paddingTop = 150.dp
    ),
    TvSlide(
        id = "video17",
        video = MyVideo(
            name = "video17.mp4",
            thumbnail = Res.drawable.video17_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.Center,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video18",
        video = MyVideo(
            name = "video18.mp4",
            thumbnail = Res.drawable.video18_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.Center,
        paddingBottom = 150.dp
    ),
    TvSlide(
        id = "video19",
        video = MyVideo(
            name = "video19.mp4",
            thumbnail = Res.drawable.video19_thumbnail
        ),
        background = Color.White.copy(alpha = 0.6f),
        color = Color.Black,
        alignment = Alignment.Center
    ),
    TvSlide(
        id = "video20",
        video = MyVideo(
            name = "video20.mp4",
            thumbnail = Res.drawable.video20_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video21",
        video = MyVideo(
            name = "video21.mp4",
            thumbnail = Res.drawable.video21_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video22",
        video = MyVideo(
            name = "video22.mp4",
            thumbnail = Res.drawable.video22_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video23",
        video = MyVideo(
            name = "video23.mp4",
            thumbnail = Res.drawable.video23_thumbnail
        ),
        background = Color.White.copy(alpha = 0.1f),
        alignment = Alignment.BottomCenter,
        paddingBottom = 100.dp
    ),
    TvSlide(
        id = "video24",
        video = MyVideo(
            name = "video24.mp4",
            thumbnail = Res.drawable.video24_thumbnail
        ),
        background = Color.White.copy(alpha = 0.5f),
        color = Color.Black,
        alignment = Alignment.TopCenter,
        paddingTop = 110.dp
    )
)