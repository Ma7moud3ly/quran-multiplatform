package com.ma7moud3ly.quran.model

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import quran.composeapp.generated.resources.Res

data class TvSlide(
    val id: String = "",
    val video: MyVideo,
    val alignment: Alignment,
    val background: Color,
    val color: Color = Color.White,
    val paddingTop: Dp = 0.dp,
    val paddingBottom: Dp = 0.dp,
    val showVerseBackground: Boolean = true
)

data class MyVideo(
    val name: String = "",
    val thumbnail: DrawableResource
) {
    val path: String get() = Res.getUri("drawable/$name")
}


data class PlaybackStates(
    val slideControls: SlideControls,
    val slide: TvSlide,
    val isPlaying: Boolean,
    val selectedVerse: Verse?
)

sealed interface SlideControls {
    data object ShowControls : SlideControls
    data object ShowReciter : SlideControls
    data object ShowVerse : SlideControls
    data object HideAll : SlideControls
}

val SlideControls.showControls: Boolean get() = this is SlideControls.ShowControls
val SlideControls.showVerse: Boolean get() = this !is SlideControls.HideAll
val SlideControls.showReciter: Boolean get() = this is SlideControls.ShowReciter
