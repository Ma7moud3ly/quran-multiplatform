package com.ma7moud3ly.quran.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.ma7moud3ly.quran.resources.Res
import com.ma7moud3ly.quran.resources.logo

data class TvBackground(
    val id: String = "",
    val video: Video = AppVideo(),
    val alignment: Alignment = Alignment.Center,
    val background: Color = Color.White.copy(alpha = 0.2f),
    val color: Color = Color.White,
    val paddingTop: Dp = 0.dp,
    val paddingBottom: Dp = 0.dp,
    val canRemove: Boolean = false
)

interface Video {
    val path: String

    @Composable
    fun getPainter(): Painter
}

data class AppVideo(
    private val name: String = "",
    private val thumbnail: DrawableResource = Res.drawable.logo
) : Video {
    override val path: String get() = Res.getUri("drawable/$name")

    @Composable
    override fun getPainter(): Painter {
        return painterResource(thumbnail)
    }
}

data class UserVideo(
    private val videoPath: String = "",
    val thumbnailPath: String = ""
) : Video {
    override val path: String get() = videoPath

    @Composable
    override fun getPainter(): Painter {
        return rememberAsyncImagePainter(thumbnailPath)
    }
}

data class PlaybackStates(
    val tvControls: TvControls,
    val background: TvBackground?,
    val isPlaying: Boolean,
    val selectedVerse: Verse?
)

sealed interface TvControls {
    data object ShowControls : TvControls
    data object ShowHeader : TvControls
    data object ShowVerse : TvControls
    data object ShowReciter : TvControls
    data object HideAll : TvControls
}

val TvControls.showControls: Boolean get() = this is TvControls.ShowControls
val TvControls.showVerseContent: Boolean get() = this is TvControls.ShowVerse || this is TvControls.ShowHeader
val TvControls.showReciter: Boolean get() = this is TvControls.ShowReciter
val TvControls.showHeader: Boolean get() = showControls || this is TvControls.ShowHeader
