package com.ma7moud3ly.quran.platform

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.model.AppVideo
import com.ma7moud3ly.quran.model.TvBackground
import com.ma7moud3ly.quran.resources.Res
import com.ma7moud3ly.quran.resources.video1_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video2_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video3_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video4_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video5_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video6_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video7_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video8_thumbnail_jvm
import com.ma7moud3ly.quran.resources.video9_thumbnail_jvm

class JvmBackgroundVideos : BackgroundVideos {
    // drawables at src/jvmMain/composeResources/drawable
    override val backgrounds: List<TvBackground> = listOf(
        TvBackground(
            id = "slide1",
            video = AppVideo(thumbnail = Res.drawable.video1_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvBackground(
            id = "slide2",
            video = AppVideo(thumbnail = Res.drawable.video2_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvBackground(
            id = "slide3",
            video = AppVideo(thumbnail = Res.drawable.video3_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvBackground(
            id = "slide4",
            video = AppVideo(thumbnail = Res.drawable.video4_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            color = Color.Black,
            alignment = Alignment.Center
        ), TvBackground(
            id = "slide5",
            video = AppVideo(thumbnail = Res.drawable.video5_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.6f),
            color = Color.Black,
            alignment = Alignment.BottomCenter,
            paddingBottom = 150.dp
        ), TvBackground(
            id = "slide6",
            video = AppVideo(thumbnail = Res.drawable.video6_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.BottomCenter,
            paddingBottom = 150.dp
        ), TvBackground(
            id = "slide7",
            video = AppVideo(thumbnail = Res.drawable.video7_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ), TvBackground(
            id = "slide8",
            video = AppVideo(thumbnail = Res.drawable.video8_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.4f),
            color = Color.Black,
            alignment = Alignment.Center
        ), TvBackground(
            id = "slide9",
            video = AppVideo(thumbnail = Res.drawable.video9_thumbnail_jvm),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center,
            paddingBottom = 150.dp
        )
    )
}

actual fun getPlaybackVideos(): BackgroundVideos = JvmBackgroundVideos()
