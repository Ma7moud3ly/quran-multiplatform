package com.ma7moud3ly.quran.features.home.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.History
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MySurfaceColumn
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import com.ma7moud3ly.quran.ui.suraNameFontFamily
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.aspect_ratio
import quran.composeapp.generated.resources.audio_file
import quran.composeapp.generated.resources.book_mark
import quran.composeapp.generated.resources.close
import quran.composeapp.generated.resources.multiple_verses
import quran.composeapp.generated.resources.tv
import quran.composeapp.generated.resources.visibility_off

val testHistory = listOf(
    History(
        type = 1,
        date = "2023-01-01 - 12:00 AM",
        chapterId = 1,
        chapterName = "سورة الفاتحة",
        verseId = 1
    ),
    History(
        type = 2,
        date = "2023-01-02 - 11:00 AM",
        chapterId = 2,
        chapterName = "البقرة",
        reciterId = "ajamy",
        reciterName = "أحمد بن علي العجمي",
        verseId = 1
    )
)

@Preview
@Composable
private fun HistoryPagePreview() {
    AppTheme {
        HistoryPage(
            list = testHistory,
            onDeleteHistory = {},
            onOpenHistory = {}
        )
    }
}

@Preview
@Composable
private fun HistoryPagePreviewLight() {
    AppTheme(darkTheme = false) {
        HistoryPage(
            list = testHistory,
            onDeleteHistory = {},
            onOpenHistory = {}
        )
    }
}

@Composable
internal fun HistoryPage(
    list: List<History>,
    onDeleteHistory: (History) -> Unit,
    onOpenHistory: (History) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LifecycleResumeEffect(LocalLifecycleOwner) {
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
        onPauseOrDispose { }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(list) {
            ItemHistory(
                history = it,
                onDelete = { onDeleteHistory(it) },
                onClick = { onOpenHistory(it) }
            )
        }
    }
}

@Composable
private fun ItemHistory(
    history: History,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    MySurfaceColumn(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        surfaceModifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        space = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    if (history.isReading) Res.drawable.book_mark
                    else Res.drawable.audio_file
                ),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = Chapter(history.chapterId).chapterFullName(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontFamily = suraNameFontFamily(),
                fontSize = 30.sp
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = onDelete,
                modifier = Modifier.offset(x = (8).dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.close),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        history.reciterName?.let {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = history.reciterName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.weight(1f))
                if (history.reelMode) Icon(
                    painter = painterResource(Res.drawable.aspect_ratio),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                if (history.playInBackground) Icon(
                    painter = painterResource(Res.drawable.visibility_off),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    painter = painterResource(
                        if (history.isNormalScreen)
                            Res.drawable.multiple_verses
                        else Res.drawable.tv
                    ),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = history.date,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = history.verseId.asVerseNumber(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 35.sp,
                fontFamily = hafsSmartFamily()
            )
        }
    }
}
