package com.ma7moud3ly.quran.features.reading.modes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.managers.VersesManager
import com.ma7moud3ly.quran.model.AppFont
import com.ma7moud3ly.quran.model.testReadingSettings
import com.ma7moud3ly.quran.model.testVersesManager
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.SwipeableBox
import com.ma7moud3ly.quran.ui.isCompactDevice
import org.jetbrains.compose.ui.tooling.preview.Preview



@Preview
@Composable
private fun SectionSingleVersePreview() {
    AppTheme(darkTheme = true) {
        Surface {
            Column {
                SectionSingleVerse(
                    versesManager = testVersesManager,
                    font = testReadingSettings.font,
                    onCopyVerse = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun SectionSingleVersePreviewLight() {
    AppTheme(darkTheme = false) {
        Surface {
            Column {
                SectionSingleVerse(
                    versesManager = testVersesManager,
                    font = testReadingSettings.font,
                    onCopyVerse = {}
                )
            }
        }
    }
}

@Composable
internal fun ColumnScope.SectionSingleVerse(
    versesManager: VersesManager,
    showNavigation: Boolean = true,
    font: AppFont,
    onCopyVerse: () -> Unit
) {

    val selectedVerse by versesManager.selectedVerse.collectAsState(null)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showNavigation && isCompactDevice().not()) {
            IconButton(onClick = versesManager::previousVerse) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        SwipeableBox(
            modifier = Modifier.weight(1f),
            verticalScroll = false,
            contentAlignment = Alignment.Center,
            onSwipeRight = versesManager::nextVerse,
            onSwipeLeft = versesManager::previousVerse
        ) {
            selectedVerse?.let {
                ItemVerse(
                    verse = it,
                    current = { false },
                    textAlign = TextAlign.Center,
                    autoSize = true,
                    font = font,
                    onCopyVerse = onCopyVerse,
                    onClick = {}
                )
            }
        }
        if (showNavigation && isCompactDevice().not()) {
            IconButton(onClick = versesManager::nextVerse) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
