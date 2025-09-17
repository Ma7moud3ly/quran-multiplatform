package com.ma7moud3ly.quran.features.search.reciter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.home.reciters.testReciters
import com.ma7moud3ly.quran.features.search.SearchBox
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.DialogHeader
import com.ma7moud3ly.quran.ui.MyButton
import com.ma7moud3ly.quran.ui.MyDialog
import com.ma7moud3ly.quran.ui.MySurfaceRow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.recite_reciter_select
import quran.composeapp.generated.resources.reciter_add_all
import quran.composeapp.generated.resources.search
import quran.composeapp.generated.resources.search_reciter_hint

@Preview
@Composable
private fun PickReciterDialogPreview() {
    AppTheme(darkTheme = true) {
        PickReciterDialogContent(
            allReciters = testReciters,
            selectMultiple = true,
            selectedReciterIds = listOf("1", "2"),
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
            allReciters = testReciters,
            selectMultiple = true,
            selectedReciterIds = listOf("1", "2"),
            onSelectReciter = {},
            onBack = {}
        )
    }
}

@Composable
internal fun PickReciterDialogContent(
    allReciters: List<Reciter>,
    selectMultiple: Boolean,
    selectedReciterIds: List<String>,
    onSelectReciter: (List<String>) -> Unit,
    onBack: () -> Unit
) {

    val listState = rememberLazyListState()
    LaunchedEffect(allReciters, selectedReciterIds) {
        if (selectedReciterIds.isNotEmpty() && allReciters.isNotEmpty()) {
            // find the index of first selected reciter (according to the order of allReciters)
            // and then scroll to it
            for (index in 0..allReciters.size - 1) {
                val reciter = allReciters[index]
                if (selectedReciterIds.contains(reciter.id)) {
                    listState.scrollToItem(index)
                    break
                }
            }
        }
    }
    val selectedReciters = remember { mutableStateSetOf<String>() }
    LaunchedEffect(Unit) {
        selectedReciters.addAll(selectedReciterIds)
    }

    fun addReciter(reciterId: String) {
        if (selectMultiple) {
            if (selectedReciters.contains(reciterId)) {
                selectedReciters.remove(reciterId)
            } else {
                selectedReciters.add(reciterId)
            }
        } else {
            onSelectReciter(listOf(reciterId))
        }
    }

    MyDialog(
        onDismissRequest = onBack,
        header = {
            DialogHeader(
                text = stringResource(Res.string.search),
                onBack = onBack
            )
        },
        footer = {
            if (selectMultiple) Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                MyButton(
                    text = Res.string.reciter_add_all,
                    onClick = { onSelectReciter(selectedReciters.toList()) },
                    enabled = { selectedReciters.isNotEmpty() }
                )
            }
        },
        modifier = Modifier.padding(16.dp),
    ) {
        var query by remember { mutableStateOf("") }
        val filteredList by remember(query, allReciters) {
            derivedStateOf {
                val query = query.trim()
                if (query.isEmpty()) allReciters
                else allReciters.filter { it.name.contains(query) }
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
                        showCheckBox = selectMultiple,
                        selected = { selectedReciters.contains(it.id) },
                        onClick = { addReciter(it.id) }
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
    selected: () -> Boolean = { false },
    showArrow: Boolean = false,
    showCheckBox: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: (() -> Unit)?,
) {
    val isSelected = selected()
    MySurfaceRow(
        color = if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        else MaterialTheme.colorScheme.surfaceContainerHigh,
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
        if (showCheckBox) {
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(
                checked = isSelected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.secondary,
                    checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}