package com.ma7moud3ly.quran.features.home.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ma7moud3ly.quran.model.Bookmark
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MySurfaceColumn
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import com.ma7moud3ly.quran.ui.suraNameFontFamily
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.bookmark
import quran.composeapp.generated.resources.close
import quran.composeapp.generated.resources.home_bookmarks_empty

val testBookmarks = listOf(
    Bookmark(
        date = "2023-01-01 - 12:00 AM",
        chapterId = 1,
        verseId = 1
    ),
    Bookmark(
        date = "2023-01-02 - 11:00 AM",
        chapterId = 2,
        verseId = 1
    )
)

@Preview
@Composable
private fun BookmarksPagePreview() {
    AppTheme {
        BookmarksPage(
            list = testBookmarks,
            onDeleteBookmark = {},
            onOpenBookmark = {}
        )
    }
}

@Preview
@Composable
private fun BookmarksPagePreviewLight() {
    AppTheme(darkTheme = false) {
        BookmarksPage(
            list = testBookmarks,
            onDeleteBookmark = {},
            onOpenBookmark = {}
        )
    }
}

@Composable
internal fun BookmarksPage(
    list: List<Bookmark>,
    onDeleteBookmark: (Bookmark) -> Unit,
    onOpenBookmark: (Bookmark) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LifecycleResumeEffect(LocalLifecycleOwner) {
        coroutineScope.launch {
            listState.animateScrollToItem(0)
        }
        onPauseOrDispose { }
    }
    if (list.isEmpty()) {
        EmptyItems(
            title = stringResource(Res.string.home_bookmarks_empty),
            icon = Icons.Outlined.BookmarkBorder
        )
    } else LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(list) {
            ItemBookmarks(
                bookmark = it,
                onDelete = { onDeleteBookmark(it) },
                onClick = { onOpenBookmark(it) }
            )
        }
    }
}

@Composable
private fun ItemBookmarks(
    bookmark: Bookmark,
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
                painter = painterResource(Res.drawable.bookmark),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = Chapter(bookmark.chapterId).chapterFullName(),
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
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = bookmark.date,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = bookmark.verseId.asVerseNumber(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 35.sp,
                fontFamily = hafsSmartFamily()
            )
        }
    }
}


@Composable
fun EmptyItems(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}