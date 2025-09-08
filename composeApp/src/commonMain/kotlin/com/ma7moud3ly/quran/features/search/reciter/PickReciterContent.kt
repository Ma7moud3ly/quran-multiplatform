package com.ma7moud3ly.quran.features.search.reciter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.features.home.reciters.testReciters
import com.ma7moud3ly.quran.features.search.SearchBox
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.DialogHeader
import com.ma7moud3ly.quran.ui.MyDialog
import com.ma7moud3ly.quran.ui.MySurfaceRow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.recite_reciter_select
import quran.composeapp.generated.resources.search
import quran.composeapp.generated.resources.search_reciter_hint

@Preview
@Composable
private fun PickReciterDialogPreview() {
    AppTheme(darkTheme = true) {
        PickReciterDialogContent(
            list = testReciters,
            onSelectReciter = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
private fun PickReciterDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        PickReciterDialogContent(
            list = testReciters,
            onSelectReciter = {},
            onBack = {}
        )
    }
}

@Composable
internal fun PickReciterDialogContent(
    list: List<Reciter>,
    filterReciters: Boolean = false,
    selectedReciterId: String = "",
    onSelectReciter: (Reciter) -> Unit,
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
        val listState = rememberLazyListState()
        LaunchedEffect(list, selectedReciterId) {
            if (selectedReciterId.isNotEmpty() && list.isNotEmpty()) {
                val index = list.indexOfFirst { it.id == selectedReciterId }
                if (index > 0) listState.scrollToItem(index)
            }
        }

        var query by remember { mutableStateOf("") }
        val filteredList by remember(query, list) {
            derivedStateOf {
                list.filter {
                    if (query.isEmpty()) {
                        if (filterReciters) it.canListen else true
                    } else {
                        (if (filterReciters) it.canListen else true) &&
                                it.name.contains(query.trim(), ignoreCase = true)
                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchBox(
                placeholder = Res.string.search_reciter_hint,
                showKeyboard = false,
                onSearch = { query = it }
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
                state = listState
            ) {
                items(filteredList) {
                    ItemReciter(
                        reciter = it,
                        onClick = { onSelectReciter(it) }
                    )
                }
            }
        }
    }
}


@Composable
internal fun ItemReciter(
    reciter: Reciter?,
    modifier: Modifier = Modifier.fillMaxWidth(),
    showArrow: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: (() -> Unit)?,
) {
    MySurfaceRow(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            Alignment.CenterHorizontally
        ),
        onClick = onClick
    ) {
        if (reciter != null) Text(
            text = reciter.name,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = color
        ) else Text(
            text = stringResource(Res.string.recite_reciter_select),
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