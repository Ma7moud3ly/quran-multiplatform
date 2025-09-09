package com.ma7moud3ly.quran.features.home.index

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.platform.isAndroid
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.LocalPlatform
import com.ma7moud3ly.quran.ui.MySurface
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.suraNameFontFamily
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.audio_file
import quran.composeapp.generated.resources.index_madina
import quran.composeapp.generated.resources.index_mecca
import quran.composeapp.generated.resources.index_verse
import quran.composeapp.generated.resources.index_verses

internal val testChaptersIndex = listOf(
    Chapter(1, "الفاتحة", "", "meccan", count = 7),
    Chapter(2, "البقرة", "", "medinan", count = 286),
    Chapter(3, "آل عمران", "", "medinan", count = 200),
    Chapter(4, "النساء", "", "medinan", count = 176)
)


@Preview
@Composable
private fun ChaptersIndexPagePreview() {
    AppTheme {
        ChaptersIndexPage(
            list = testChaptersIndex,
            onOpenChapter = {},
            onPlayChapter = {}
        )
    }
}

@Preview
@Composable
private fun ChaptersIndexPagePreviewLight() {
    AppTheme(darkTheme = false) {
        ChaptersIndexPage(
            list = testChaptersIndex,
            onOpenChapter = {},
            onPlayChapter = {}
        )
    }
}

@Composable
internal fun ChaptersIndexPage(
    list: List<Chapter>,
    onOpenChapter: (Chapter) -> Unit,
    onPlayChapter: (Chapter) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) { chapter ->
            ItemChapterIndex(
                chapter = chapter,
                onOpen = { onOpenChapter(chapter) },
                onPlay = { onPlayChapter(chapter) }
            )
        }
    }
}


@Composable
internal fun ItemChapterIndex(
    chapter: Chapter,
    onOpen: () -> Unit,
    onPlay: () -> Unit
) {
    MySurfaceRow(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth().padding(
            horizontal = 16.dp,
            vertical = 4.dp
        ),
        color = MaterialTheme.colorScheme.surface,
        surfaceModifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndexNumber(
            number = chapter.id,
            color = MaterialTheme.colorScheme.background
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                Alignment.CenterVertically
            )
        ) {
            Text(
                text = chapter.chapterFullName(),
                style = MaterialTheme.typography.titleSmall,
                fontFamily = suraNameFontFamily(),
                fontSize = 33.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.offset(y = (0).dp),
            )
            val location = stringResource(
                if (chapter.isMeccan) Res.string.index_mecca
                else Res.string.index_madina
            )
            //doesn't work
            /*val verses = pluralStringResource(
                Res.plurals.index_verses_number,
                chapter.count,
                chapter.count
            )*/
            val verses = stringResource(
                if (chapter.count >= 11) Res.string.index_verse
                else Res.string.index_verses,
                chapter.count
            )
            val platform = LocalPlatform.current
            Text(
                text = "$location - $verses",
                modifier = Modifier.offset(y = (0).dp),
                style = if (platform.isAndroid) MaterialTheme.typography.bodySmall
                else MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
            )
        }
        RoundButton(
            icon = Res.drawable.audio_file,
            background = MaterialTheme.colorScheme.surfaceContainerHigh,
            onClick = onPlay,
            iconSize = 24.dp
        )
    }
}

@Composable
fun IndexNumber(
    number: Int,
    color: Color = MaterialTheme.colorScheme.background,
    size: Dp = 50.dp
) {
    MySurface(
        shape = CircleShape,
        modifier = Modifier.size(size),
        color = color
    ) {
        Text(
            text = "$number",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 18.sp
        )
    }
}