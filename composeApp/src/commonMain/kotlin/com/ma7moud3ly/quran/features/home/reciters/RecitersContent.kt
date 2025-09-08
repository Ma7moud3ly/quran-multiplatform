package com.ma7moud3ly.quran.features.home.reciters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.home.index.IndexNumber
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MySurfaceRow
import org.jetbrains.compose.ui.tooling.preview.Preview

val testReciters = listOf(
    Reciter(
        id = "1",
        name = "أبو بكر الشاطري",
    ),
    Reciter(
        id = "2",
        name = "أحمد بن علي العجمي",
    ),
    Reciter(
        id = "3",
        name = "توفيق الصايغ",
    ),
    Reciter(
        id = "4",
        name = "سعد الغامدي",
    ),
    Reciter(
        id = "5",
        name = "سعود الشريم",
    ),
)

@Preview
@Composable
private fun RecitersPagePreview() {
    AppTheme {
        RecitersPage(
            list = testReciters,
            onOpenReciter = {}
        )
    }
}

@Preview
@Composable
private fun RecitersPagePreviewLight() {
    AppTheme(darkTheme = false) {
        RecitersPage(
            list = testReciters,
            onOpenReciter = {}
        )
    }
}

@Composable
internal fun RecitersPage(
    list: List<Reciter>,
    onOpenReciter: (Reciter) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(list) { index, reciter ->
            ItemReciter(
                index = index + 1,
                reciter = reciter,
                onClick = { onOpenReciter(reciter) }
            )
        }
    }
}

@Composable
private fun ItemReciter(
    index: Int,
    reciter: Reciter,
    onClick: () -> Unit
) {
    MySurfaceRow(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(
            horizontal = 8.dp,
            vertical = 4.dp
        ),
        color = MaterialTheme.colorScheme.surface,
        surfaceModifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndexNumber(
            number = index,
            color = MaterialTheme.colorScheme.background
        )
        Column {
            BasicText(
                text = reciter.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 8.sp,
                    maxFontSize = 14.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}

