package com.ma7moud3ly.quran.platform

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.ma7moud3ly.quran.model.AppVideo
import com.ma7moud3ly.quran.model.TvBackground
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

class WasmJsBackgroundVideos : BackgroundVideos {
    // drawables at src/wasmJsMain/composeResources/drawable
    override val backgrounds: List<TvBackground> = listOf(
        TvBackground(
            id = "slide0",
            video = AppVideo(
                thumbnail = Res.drawable.video0_thumbnail_wasm,
                name = "video0_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),
        TvBackground(
            id = "slide1",
            video = AppVideo(
                thumbnail = Res.drawable.video1_thumbnail_wasm,
                name = "video1_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),
        TvBackground(
            id = "slide2",
            video = AppVideo(
                thumbnail = Res.drawable.video2_thumbnail_wasm,
                name = "video2_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide3",
            video = AppVideo(
                thumbnail = Res.drawable.video3_thumbnail_wasm,
                name = "video3_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide4",
            video = AppVideo(
                thumbnail = Res.drawable.video4_thumbnail_wasm,
                name = "video4_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide5",
            video = AppVideo(
                thumbnail = Res.drawable.video5_thumbnail_wasm,
                name = "video5_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide6",
            video = AppVideo(
                thumbnail = Res.drawable.video6_thumbnail_wasm,
                name = "video6_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide7",
            video = AppVideo(
                thumbnail = Res.drawable.video7_thumbnail_wasm,
                name = "video7_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide8",
            video = AppVideo(
                thumbnail = Res.drawable.video8_thumbnail_wasm,
                name = "video8_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide9",
            video = AppVideo(
                thumbnail = Res.drawable.video9_thumbnail_wasm,
                name = "video9_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide10",
            video = AppVideo(
                thumbnail = Res.drawable.video10_thumbnail_wasm,
                name = "video10_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        ),

        TvBackground(
            id = "slide11",
            video = AppVideo(
                thumbnail = Res.drawable.video11_thumbnail_wasm,
                name = "video11_wasm.mp4"
            ),
            background = Color.White.copy(alpha = 0.2f),
            alignment = Alignment.Center
        )
    )
}

actual fun getPlaybackVideos(): BackgroundVideos = WasmJsBackgroundVideos()
