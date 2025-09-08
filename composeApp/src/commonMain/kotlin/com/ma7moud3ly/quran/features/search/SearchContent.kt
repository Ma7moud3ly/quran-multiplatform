package com.ma7moud3ly.quran.features.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.home.index.ItemChapterIndex
import com.ma7moud3ly.quran.features.home.index.testChaptersIndex
import com.ma7moud3ly.quran.features.home.reciters.testReciters
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.features.search.reciter.ItemReciter
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.SearchQuery
import com.ma7moud3ly.quran.model.SearchResult
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MyScreen
import com.ma7moud3ly.quran.ui.MySurfaceColumn
import com.ma7moud3ly.quran.ui.ScreenHeader
import com.ma7moud3ly.quran.ui.hafsBoldFamily
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import com.ma7moud3ly.quran.ui.isCompactDevice
import com.ma7moud3ly.quran.ui.suraNameFontFamily
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.audio_file
import quran.composeapp.generated.resources.search_chapters
import quran.composeapp.generated.resources.search_hint
import quran.composeapp.generated.resources.search_reciters
import quran.composeapp.generated.resources.search_results
import quran.composeapp.generated.resources.search_verses

private val testSearchResultLists = listOf(
    SearchResult(
        chapterId = 1,
        chapterName = "الفاتحة",
        verseId = 1,
        content = "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ",
        selectionBegin = 7,
        selectionEnd = 15
    ),
    SearchResult(
        chapterId = 1,
        chapterName = "الفاتحة",
        verseId = 2,
        content = "ٱلۡحَمۡدُ لِلَّهِ رَبِّ ٱلۡعَٰلَمِينِ",
        selectionBegin = 8,
        selectionEnd = 18
    )
)

@Preview
@Composable
private fun SearchDialogPreview() {
    AppTheme(darkTheme = true) {
        SearchScreenContent(
            versesFlow = flowOf(testSearchResultLists),
            chaptersFlow = flowOf(testChaptersIndex.subList(0, 1)),
            recitersFlow = flowOf(testReciters.subList(0, 1)),
            onSearch = {},
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun SearchDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        SearchScreenContent(
            versesFlow = flowOf(testSearchResultLists),
            chaptersFlow = flowOf(testChaptersIndex.subList(0, 1)),
            recitersFlow = flowOf(testReciters.subList(0, 1)),
            onSearch = {},
            uiEvents = {}
        )
    }
}

@Composable
internal fun SearchScreenContent(
    versesFlow: Flow<List<SearchResult>?>,
    chaptersFlow: Flow<List<Chapter>?>,
    recitersFlow: Flow<List<Reciter>?>,
    onSearch: (SearchQuery) -> Unit,
    uiEvents: (SearchEvents) -> Unit
) {

    val verses by versesFlow.collectAsState(null)
    val chapters by chaptersFlow.collectAsState(null)
    val reciters by recitersFlow.collectAsState(null)
    val includeVerses = rememberSaveable { mutableStateOf(true) }
    val includeChapters = rememberSaveable { mutableStateOf(true) }
    val includeReciters = rememberSaveable { mutableStateOf(true) }

    val resultsCount by derivedStateOf {
        if (verses == null && chapters == null && reciters == null) null
        else {
            var count = 0
            if (includeVerses.value) verses?.size?.let { count += it }
            if (includeChapters.value) chapters?.size?.let { count += it }
            if (includeReciters.value) reciters?.size?.let { count += it }
            count
        }
    }

    MyScreen(
        topBar = {
            Header(
                verses = includeVerses,
                chapters = includeChapters,
                reciters = includeReciters,
                onClose = { uiEvents(SearchEvents.OnBack) },
                onSearch = onSearch
            )
        },
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        space = 16.dp
    ) {

        resultsCount?.let { count ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.search_results),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (includeChapters.value) {
                items(chapters.orEmpty()) {
                    ItemChapterIndex(
                        chapter = it,
                        onOpen = {
                            val event = SearchEvents.OpenChapter(it.id)
                            uiEvents(event)
                        },
                        onPlay = {
                            val event = SearchEvents.OpenChapter(it.id, listen = true)
                            uiEvents(event)
                        }
                    )
                }
            }
            if (includeReciters.value) {
                item{
                    FlowRow(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        reciters?.forEach {
                            ItemReciter(
                                reciter = it,
                                modifier = Modifier,
                                onClick = {
                                    val event = SearchEvents.OpenReciter(it)
                                    uiEvents(event)
                                }
                            )
                        }
                    }
                }
            }

            if (includeVerses.value) {
                items(verses.orEmpty()) {
                    ItemVerseSearch(
                        searchResult = it,
                        onOpen = {
                            val event = SearchEvents.OpenVerse(it, listen = false)
                            uiEvents(event)
                        },
                        onListen = {
                            val event = SearchEvents.OpenVerse(it, listen = true)
                            uiEvents(event)
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun ItemVerseSearch(
    searchResult: SearchResult,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onOpen: () -> Unit,
    onListen: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        append(searchResult.content)
        append(" ")
        withStyle(style = SpanStyle(fontFamily = hafsSmartFamily())) {
            append(searchResult.verseId.asVerseNumber())
        }
    }

    MySurfaceColumn(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        space = 8.dp,
        onClick = onOpen
    ) {
        Text(
            text = annotatedString,
            fontFamily = hafsBoldFamily(),
            style = MaterialTheme.typography.titleSmall,
            fontSize = 28.sp,
            lineHeight = 50.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(8.dp),
            color = color
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Chapter(searchResult.chapterId).chapterFullName(),
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = suraNameFontFamily(),
                fontSize = 25.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            RoundButton(
                icon = Res.drawable.audio_file,
                background = MaterialTheme.colorScheme.surfaceContainerHigh,
                onClick = onListen,
                iconSize = 24.dp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    verses: MutableState<Boolean>,
    chapters: MutableState<Boolean>,
    reciters: MutableState<Boolean>,
    onSearch: (SearchQuery) -> Unit,
    onClose: () -> Unit
) {

    fun onInitSearch(query: String) {
        onSearch(
            SearchQuery(
                query = query,
                verses = verses.value,
                chapters = chapters.value,
                reciters = reciters.value
            )
        )
    }
    if (isCompactDevice()) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            ScreenHeader(
                text = "",
                icon = Icons.AutoMirrored.Default.ArrowBack,
                onBack = onClose,
                showDivider = false
            )
            SearchBox(
                placeholder = Res.string.search_hint,
                onSearch = ::onInitSearch
            )
            SectionSearchCategories(
                verses = verses,
                chapters = chapters,
                reciters = reciters
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Row(
                modifier = Modifier.statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SearchBox(
                    modifier = Modifier.weight(1f),
                    placeholder = Res.string.search_hint,
                    onSearch = ::onInitSearch
                )
                RoundButton(
                    icon = Icons.AutoMirrored.Default.ArrowForward,
                    onClick = onClose,
                    background = Color.Transparent
                )
            }
            SectionSearchCategories(
                verses = verses,
                chapters = chapters,
                reciters = reciters
            )
        }
    }
}


@OptIn(FlowPreview::class)
@Composable
internal fun SearchBox(
    placeholder: StringResource,
    modifier: Modifier = Modifier.fillMaxWidth(),
    showKeyboard: Boolean = true,
    onSearch: (String) -> Unit
) {
    var search by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (showKeyboard && search.isBlank()) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    OutlinedTextField(
        modifier = modifier.focusRequester(focusRequester),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.onPrimary,
        ),
        value = search,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(search)
                keyboardController?.hide()
            }
        ),
        maxLines = 1,
        onValueChange = { search = it },
        placeholder = {
            BasicText(
                text = stringResource(placeholder),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.tertiary
                ),
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    11.sp,
                    16.sp
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = ""
            )
        },
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(onClick = {
                    onSearch("")
                    search = ""
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        tint = MaterialTheme.colorScheme.tertiary,
                        contentDescription = ""
                    )
                }
            }
        }
    )
}

@Composable
private fun SectionSearchCategories(
    verses: MutableState<Boolean>,
    chapters: MutableState<Boolean>,
    reciters: MutableState<Boolean>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MyCheckBox(
            label = Res.string.search_verses,
            checked = verses
        )
        MyCheckBox(
            label = Res.string.search_chapters,
            checked = chapters
        )
        MyCheckBox(
            label = Res.string.search_reciters,
            checked = reciters
        )
    }
}

@Composable
private fun MyCheckBox(
    label: StringResource,
    checked: MutableState<Boolean>
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked.value,
            onCheckedChange = { checked.value = it },
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                checkedColor = MaterialTheme.colorScheme.secondary,
                uncheckedColor = MaterialTheme.colorScheme.tertiary
            )
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}