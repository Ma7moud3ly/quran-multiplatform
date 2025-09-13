package com.ma7moud3ly.quran.platform

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.model.MyVideo
import com.ma7moud3ly.quran.model.TvSlide
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.video1_thumbnail_jvm
import quran.composeapp.generated.resources.video2_thumbnail_jvm
import quran.composeapp.generated.resources.video3_thumbnail_jvm
import quran.composeapp.generated.resources.video4_thumbnail_jvm
import quran.composeapp.generated.resources.video5_thumbnail_jvm
import quran.composeapp.generated.resources.video6_thumbnail_jvm
import quran.composeapp.generated.resources.video7_thumbnail_jvm
import quran.composeapp.generated.resources.video8_thumbnail_jvm
import quran.composeapp.generated.resources.video9_thumbnail_jvm

class JvmPlaybackVideos : PlaybackVideos {
    // drawables at src/jvmMain/composeResources/drawable
    override val slides: List<TvSlide> = listOf(
        TvSlide(
            id = "slide1",
            video = MyVideo(thumbnail = Res.drawable.video1_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvSlide(
            id = "slide2",
            video = MyVideo(thumbnail = Res.drawable.video2_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvSlide(
            id = "slide3",
            video = MyVideo(thumbnail = Res.drawable.video3_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvSlide(
            id = "slide4",
            video = MyVideo(thumbnail = Res.drawable.video4_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            color = Color.Black,
            alignment = Alignment.Center
        ), TvSlide(
            id = "slide5",
            video = MyVideo(thumbnail = Res.drawable.video5_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.6f),
            color = Color.Black,
            alignment = Alignment.BottomCenter,
            paddingBottom = 150.dp
        ), TvSlide(
            id = "slide6",
            video = MyVideo(thumbnail = Res.drawable.video6_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.BottomCenter,
            paddingBottom = 150.dp
        ), TvSlide(
            id = "slide7",
            video = MyVideo(thumbnail = Res.drawable.video7_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvSlide(
            id = "slide8",
            video = MyVideo(thumbnail = Res.drawable.video8_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.4f),
            color = Color.Black,
            alignment = Alignment.Center
        ), TvSlide(
            id = "slide9",
            video = MyVideo(thumbnail = Res.drawable.video9_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center,
            paddingBottom = 150.dp
        )
    )
}

actual fun getPlaybackVideos(): PlaybackVideos = JvmPlaybackVideos()
