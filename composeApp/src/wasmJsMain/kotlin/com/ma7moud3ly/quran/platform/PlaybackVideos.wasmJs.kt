package com.ma7moud3ly.quran.platform

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.ma7moud3ly.quran.model.MyVideo
import com.ma7moud3ly.quran.model.TvSlide
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.video0_thumbnail_wasm
import quran.composeapp.generated.resources.video10_thumbnail_wasm
import quran.composeapp.generated.resources.video11_thumbnail_wasm
import quran.composeapp.generated.resources.video1_thumbnail_wasm
import quran.composeapp.generated.resources.video2_thumbnail_wasm
import quran.composeapp.generated.resources.video3_thumbnail_wasm
import quran.composeapp.generated.resources.video4_thumbnail_wasm
import quran.composeapp.generated.resources.video5_thumbnail_wasm
import quran.composeapp.generated.resources.video6_thumbnail_wasm
import quran.composeapp.generated.resources.video7_thumbnail_wasm
import quran.composeapp.generated.resources.video8_thumbnail_wasm
import quran.composeapp.generated.resources.video9_thumbnail_wasm

class WasmJsPlaybackVideos : PlaybackVideos {
    // drawables at src/wasmJsMain/composeResources/drawable
    override val slides: List<TvSlide> = listOf(
        TvSlide(
            id = "slide0",
            video = MyVideo(
                thumbnail = Res.drawable.video0_thumbnail_wasm,
                name = "video0_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),
        TvSlide(
            id = "slide1",
            video = MyVideo(
                thumbnail = Res.drawable.video1_thumbnail_wasm,
                name = "video1_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),
        TvSlide(
            id = "slide2",
            video = MyVideo(
                thumbnail = Res.drawable.video2_thumbnail_wasm,
                name = "video2_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide3",
            video = MyVideo(
                thumbnail = Res.drawable.video3_thumbnail_wasm,
                name = "video3_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide4",
            video = MyVideo(
                thumbnail = Res.drawable.video4_thumbnail_wasm,
                name = "video4_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide5",
            video = MyVideo(
                thumbnail = Res.drawable.video5_thumbnail_wasm,
                name = "video5_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide6",
            video = MyVideo(
                thumbnail = Res.drawable.video6_thumbnail_wasm,
                name = "video6_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide7",
            video = MyVideo(
                thumbnail = Res.drawable.video7_thumbnail_wasm,
                name = "video7_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide8",
            video = MyVideo(
                thumbnail = Res.drawable.video8_thumbnail_wasm,
                name = "video8_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide9",
            video = MyVideo(
                thumbnail = Res.drawable.video9_thumbnail_wasm,
                name = "video9_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide10",
            video = MyVideo(
                thumbnail = Res.drawable.video10_thumbnail_wasm,
                name = "video10_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvSlide(
            id = "slide11",
            video = MyVideo(
                thumbnail = Res.drawable.video11_thumbnail_wasm,
                name = "video11_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        )
    )
}

actual fun getPlaybackVideos(): PlaybackVideos = WasmJsPlaybackVideos()
