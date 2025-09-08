package com.ma7moud3ly.quran.features.reading

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.managers.VersesManager
import com.ma7moud3ly.quran.model.testVersesManager
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun VersesScrollbarPreview() {
    AppTheme {
        Surface {
            VersesScrollbar(
                versesManager = testVersesManager
            )
        }
    }
}

@Composable
internal fun VersesScrollbar(
    modifier: Modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
    versesManager: VersesManager
) {
    val selectedVerse by versesManager.selectedVerse.collectAsState(null)
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        versesManager.handleVersesScrollbar(listState)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        HorizontalDivider()
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            state = listState
        ) {
            versesManager.verses.forEach { verse ->
                item {
                    ItemNumber(
                        verseNumber = verse.verseNumber,
                        selected = { verse.id == selectedVerse?.id },
                        onSelect = { versesManager.selectVerse(verse) }
                    )
                }
            }
        }
    }
}


@Composable
private fun ItemNumber(
    verseNumber: String,
    selected: () -> Boolean,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        color = Color.Transparent,
        modifier = Modifier.clickable(
            onClick = onSelect,
            enabled = true,
            indication = null,
            interactionSource = interactionSource
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(70.dp)
                .clipToBounds()
        ) {
            Text(
                text = verseNumber,
                color = if (selected()) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.onPrimary,
                fontFamily = hafsSmartFamily(),
                lineHeight = 20.sp,
                fontSize = 50.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .offset(y = (-18).dp)
            )
        }
    }
}