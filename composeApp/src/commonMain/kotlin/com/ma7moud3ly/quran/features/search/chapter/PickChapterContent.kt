package com.ma7moud3ly.quran.features.search.chapter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.features.home.index.testChaptersIndex
import com.ma7moud3ly.quran.features.search.SearchBox
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.DialogHeader
import com.ma7moud3ly.quran.ui.MyDialog
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.suraNameFontFamily
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.recite_chapter_select
import quran.composeapp.generated.resources.search
import quran.composeapp.generated.resources.search_chapter_hint

@Preview
@Composable
private fun PickChapterDialogPreview() {
    AppTheme(darkTheme = true) {
        PickChapterDialogContent(
            list = testChaptersIndex,
            onSelectChapter = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun PickChapterDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        PickChapterDialogContent(
            list = testChaptersIndex,
            onSelectChapter = {},
            onBack = {}
        )
    }
}

@Composable
internal fun PickChapterDialogContent(
    list: List<Chapter>,
    showKeyboard: Boolean = true,
    selectedChapterId: Int = 1,
    onSelectChapter: (Int) -> Unit,
    onBack: () -> Unit
) {
    MyDialog(
        onDismissRequest = onBack,
        header = {
            DialogHeader(
                text = stringResource(Res.string.search),
                onBack = onBack
            )
        },
        modifier = Modifier.padding(16.dp),
    ) {
        val listState = rememberLazyListState(selectedChapterId - 1)
        var query by remember { mutableStateOf("") }
        val filteredList by remember(query, list) {
            derivedStateOf {
                list.filter {
                    if (query.isEmpty()) {
                        true
                    } else {
                        it.name.contains(query.trim(), ignoreCase = true)
                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchBox(
                placeholder = Res.string.search_chapter_hint,
                showKeyboard = showKeyboard,
                onSearch = { query = it }
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(filteredList) {
                    ItemChapter(
                        chapter = it,
                        onClick = { onSelectChapter(it.id) }
                    )
                }
            }
        }
    }
}



@Composable
internal fun ItemChapter(
    chapter: Chapter?,
    showArrow: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: (() -> Unit)? = null,
) {
    MySurfaceRow(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            Alignment.CenterHorizontally
        ),
        onClick = onClick
    ) {
        if (chapter != null) Text(
            text = chapter.chapterFullName(),
            style = MaterialTheme.typography.titleSmall,
            fontFamily = suraNameFontFamily(),
            fontSize = 33.sp,
            color = color
        ) else Text(
            text = stringResource(Res.string.recite_chapter_select),
            style = MaterialTheme.typography.titleSmall,
            fontSize = 14.sp,
            color = color
        )
        if (showArrow) Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "",
            tint = color
        )
    }
}