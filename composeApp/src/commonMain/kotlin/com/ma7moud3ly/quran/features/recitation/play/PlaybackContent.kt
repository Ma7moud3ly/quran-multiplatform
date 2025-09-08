package com.ma7moud3ly.quran.features.recitation.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ma7moud3ly.quran.ui.isCompactDevice


@Composable
fun PlaybackScreenContent(
    enableReelMode: Boolean,
    background: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = if (enableReelMode && isCompactDevice()) {
            Modifier.fillMaxSize().background(background).aspectRatio(9f / 16f)
        } else {
            Modifier
        },
        color = background,
        content = content
    )
}