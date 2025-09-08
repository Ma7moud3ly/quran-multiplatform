package com.ma7moud3ly.quran.features.recitation.config

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.features.home.reciters.testReciters
import com.ma7moud3ly.quran.features.reading.SuraName
import com.ma7moud3ly.quran.features.recitation.config.verses.SelectVerseNumberDialog
import com.ma7moud3ly.quran.features.search.chapter.ItemChapter
import com.ma7moud3ly.quran.features.search.reciter.ItemReciter
import com.ma7moud3ly.quran.features.settings.SettingsLabel
import com.ma7moud3ly.quran.model.Chapter
import com.ma7moud3ly.quran.model.ScreenMode
import com.ma7moud3ly.quran.model.RecitationState
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.model.testChapter
import com.ma7moud3ly.quran.model.testDownloadedChapter
import com.ma7moud3ly.quran.model.testRecitationState
import com.ma7moud3ly.quran.platform.getPlatform
import com.ma7moud3ly.quran.platform.isAndroid
import com.ma7moud3ly.quran.platform.isMobile
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MyButton
import com.ma7moud3ly.quran.ui.MyScreen
import com.ma7moud3ly.quran.ui.MySurfaceColumn
import com.ma7moud3ly.quran.ui.MySurfaceRow
import com.ma7moud3ly.quran.ui.ScreenHeader
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import com.ma7moud3ly.quran.ui.isCompactDevice
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.downloaded
import quran.composeapp.generated.resources.recite
import quran.composeapp.generated.resources.recite_chapter
import quran.composeapp.generated.resources.recite_mode_normal
import quran.composeapp.generated.resources.recite_mode_tv
import quran.composeapp.generated.resources.recite_range
import quran.composeapp.generated.resources.recite_range_from
import quran.composeapp.generated.resources.recite_range_single
import quran.composeapp.generated.resources.recite_range_to
import quran.composeapp.generated.resources.recite_start
import quran.composeapp.generated.resources.recite_verse
import quran.composeapp.generated.resources.reciter
import quran.composeapp.generated.resources.reciter_add_multiple
import quran.composeapp.generated.resources.reciter_more
import quran.composeapp.generated.resources.settings_background
import quran.composeapp.generated.resources.settings_background_description
import quran.composeapp.generated.resources.settings_reel
import quran.composeapp.generated.resources.settings_reel_description
import quran.composeapp.generated.resources.settings_shuffle
import quran.composeapp.generated.resources.settings_shuffle_description


@Preview
@Composable
private fun RecitationConfigScreenPreview() {
    AppTheme(darkTheme = true) {
        RecitationConfigScreenContent(
            snackbarHostState = remember { SnackbarHostState() },
            selectedChapter = { testChapter },
            downloadedChapters = { testDownloadedChapter },
            reciters = { testReciters },
            recitationState = { testRecitationState },
            uiEvents = {}
        )
    }
}

@Preview
@Composable
private fun RecitationConfigScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        RecitationConfigScreenContent(
            snackbarHostState = remember { SnackbarHostState() },
            selectedChapter = { testChapter },
            downloadedChapters = { testDownloadedChapter },
            reciters = { testReciters },
            recitationState = { testRecitationState },
            uiEvents = {}
        )
    }
}

@Composable
internal fun RecitationConfigScreenContent(
    snackbarHostState: SnackbarHostState,
    recitationState: () -> RecitationState,
    selectedChapter: () -> Chapter?,
    downloadedChapters: () -> List<Chapter>,
    reciters: () -> List<Reciter>,
    uiEvents: (ConfigEvents) -> Unit,
) {
    MyScreen(
        space = 16.dp,
        modifier = Modifier
            .padding(0.dp)
            .verticalScroll(rememberScrollState()),
        topBar = {
            ScreenHeader(
                text = stringResource(Res.string.recite),
                textAlign = TextAlign.Center,
                icon = Icons.Default.Close,
                onBack = { uiEvents(ConfigEvents.OnBack) }
            )
        },
        bottomBar = {
            Footer(
                onStart = { uiEvents(ConfigEvents.InitRecitation(it)) },
                enabled = { selectedChapter() != null && reciters().isNotEmpty() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        if (isCompactDevice()) {
            SectionReciters(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                reciters = reciters,
                recitationState = recitationState,
                onPickReciter = { uiEvents(ConfigEvents.PickReciters) },
                onToggleReciters = { uiEvents(ConfigEvents.ToggleReciters(it)) },
                onRemoveReciter = { uiEvents(ConfigEvents.RemoveReciter(it)) }
            )
            HorizontalDivider()
            SectionChapter(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                recitationState = recitationState,
                selectedChapter = selectedChapter,
                chapters = downloadedChapters,
                onSelectChapter = { uiEvents(ConfigEvents.SelectChapter(it)) },
                onPickChapter = { uiEvents(ConfigEvents.PickChapters) }
            )
            HorizontalDivider()
            SectionRange(
                selectedChapter = selectedChapter,
                recitationState = recitationState,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(0.5f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    SectionReciters(
                        modifier = Modifier.padding(16.dp),
                        reciters = reciters,
                        recitationState = recitationState,
                        onPickReciter = { uiEvents(ConfigEvents.PickReciters) },
                        onToggleReciters = { uiEvents(ConfigEvents.ToggleReciters(it)) },
                        onRemoveReciter = { uiEvents(ConfigEvents.RemoveReciter(it)) }
                    )
                }
                MySurfaceColumn(
                    surfaceModifier = Modifier
                        .padding(16.dp)
                        .weight(0.5f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    SectionChapter(
                        modifier = Modifier.padding(16.dp),
                        recitationState = recitationState,
                        selectedChapter = selectedChapter,
                        chapters = downloadedChapters,
                        onSelectChapter = { uiEvents(ConfigEvents.SelectChapter(it)) },
                        onPickChapter = { uiEvents(ConfigEvents.PickChapters) }
                    )
                    SectionRange(
                        selectedChapter = selectedChapter,
                        recitationState = recitationState,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }
            }
        }


        ShuffleMode(recitationState)

        if (getPlatform().isMobile && isCompactDevice()) {
            HorizontalDivider()
            SectionReelMode(
                recitationState().reelModeState
            )
        }
        if (getPlatform().isAndroid) {
            HorizontalDivider()
            SectionPlayInBackground(
                recitationState().playInBgState
            )
        }
    }
}

@Composable
private fun SectionReciters(
    modifier: Modifier = Modifier,
    recitationState: () -> RecitationState,
    reciters: () -> List<Reciter>,
    onPickReciter: () -> Unit,
    onToggleReciters: (multiple: Boolean) -> Unit,
    onRemoveReciter: (Reciter) -> Unit
) {
    val state = recitationState()
    val canChangeReciter by remember { state.canChangeReciterState }
    val reciters = reciters()
    var multiReciters by remember { state.multiRecitersState }

    fun initMultipleReciterMode() {
        multiReciters = true
        onToggleReciters(multiReciters)
        onPickReciter()
    }

    fun initSingleReciterMode() {
        multiReciters = false
        onToggleReciters(multiReciters)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SettingsLabel(Res.string.reciter)
        if (multiReciters.not()) {
            ItemReciter(
                reciter = reciters.firstOrNull(),
                showArrow = canChangeReciter,
                onClick = if (canChangeReciter) onPickReciter else null
            )
            if (reciters.size == 1) MiniReciter(
                text = stringResource(Res.string.reciter_add_multiple),
                color = MaterialTheme.colorScheme.onSecondary,
                background = MaterialTheme.colorScheme.secondary,
                onClick = ::initMultipleReciterMode
            )
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reciters.forEach { reciter ->
                    MiniReciter(
                        text = reciter.name,
                        onIconClick = {
                            if (reciters.size == 1) initSingleReciterMode()
                            onRemoveReciter(reciter)
                        }
                    )
                }
                MiniReciter(
                    text = stringResource(Res.string.reciter_more),
                    color = MaterialTheme.colorScheme.onSecondary,
                    background = MaterialTheme.colorScheme.secondary,
                    onClick = onPickReciter
                )
            }
        }
    }
}

@Composable
private fun MiniReciter(
    text: String,
    icon: ImageVector = Icons.Default.Close,
    background: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: (() -> Unit)? = null,
    onIconClick: (() -> Unit)? = null,
) {
    MySurfaceRow(
        color = background,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        space = 8.dp,
        verticalAlignment = Alignment.CenterVertically,
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 12.sp,
            color = color
        )
        onIconClick?.let {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier.clip(CircleShape).clickable(onClick = it),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SectionChapter(
    modifier: Modifier = Modifier,
    recitationState: () -> RecitationState,
    selectedChapter: () -> Chapter?,
    chapters: () -> List<Chapter>,
    onSelectChapter: (Chapter) -> Unit,
    onPickChapter: () -> Unit
) {
    val chapter = selectedChapter()
    val state = recitationState()
    val canChangeChapter by remember { state.canChangeChapterState }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingsLabel(Res.string.recite_chapter)
        ItemChapter(
            chapter = chapter,
            showArrow = canChangeChapter,
            onClick = if (canChangeChapter) onPickChapter else null
        )
        if (canChangeChapter) {
            if (chapters().isNotEmpty()) Text(
                text = stringResource(Res.string.downloaded),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 16.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chapters().forEach {
                    SuraName(
                        chapterName = it.chapterName(),
                        onClick = { onSelectChapter(it) },
                        background = if (it.id == chapter?.id)
                            MaterialTheme.colorScheme.secondary.copy(0.2f)
                        else MaterialTheme.colorScheme.surface,
                        icon = null,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionRange(
    selectedChapter: () -> Chapter?,
    modifier: Modifier = Modifier,
    recitationState: () -> RecitationState
) {
    val chapter = selectedChapter() ?: return
    val state = recitationState()
    val start = state.firstVerseState
    val end = state.lastVerseState
    val canChangeVerse = state.canChangeVerseState.value
    var selectStartNumber by remember { mutableStateOf<Boolean?>(null) }
    var singleVerse by rememberSaveable { state.singleVerseState }

    if (selectStartNumber != null) {
        SelectVerseNumberDialog(
            start = start,
            end = end,
            selectStart = selectStartNumber!!,
            limit = chapter.count,
            onDismiss = { selectStartNumber = null }
        )
    }

    Column(modifier = modifier) {
        SettingsLabel(Res.string.recite_range)
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (singleVerse.not()) Text(
                text = stringResource(Res.string.recite_range_from),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            ) else Text(
                text = stringResource(Res.string.recite_verse),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(Modifier.weight(1f))
            Surface(
                onClick = { selectStartNumber = true },
                color = Color.Transparent,
                enabled = canChangeVerse
            ) {
                Text(
                    text = start.value.asVerseNumber(),
                    fontFamily = hafsSmartFamily(),
                    fontSize = 45.sp,
                    color = if (canChangeVerse) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.onPrimary
                )
            }
            if (singleVerse.not()) {
                Text(
                    text = stringResource(Res.string.recite_range_to),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Surface(
                    onClick = { selectStartNumber = false },
                    color = Color.Transparent
                ) {
                    Text(
                        text = end.value.asVerseNumber(),
                        fontFamily = hafsSmartFamily(),
                        fontSize = 45.sp,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.recite_range_single),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(Modifier.weight(1f))
            MySwitch(
                enabled = { singleVerse },
                onToggle = { singleVerse = singleVerse.not() }
            )
        }
    }
}

@Composable
private fun ShuffleMode(recitationState: () -> RecitationState) {
    val state = recitationState()
    if (state.multiRecitersState.value.not()
        || state.singleVerseState.value
    ) return
    var shuffleModeState by remember { state.shuffleModeState }

    HorizontalDivider()
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsLabel(Res.string.settings_shuffle)
            Spacer(modifier = Modifier.weight(1f))
            MySwitch(
                enabled = { shuffleModeState },
                onToggle = { shuffleModeState = it }
            )
        }
        Text(
            text = stringResource(Res.string.settings_shuffle_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Justify
        )
    }
}


@Composable
private fun SectionReelMode(reelModeState: MutableState<Boolean>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsLabel(Res.string.settings_reel)
            Spacer(modifier = Modifier.weight(1f))
            MySwitch(
                enabled = { reelModeState.value },
                onToggle = { reelModeState.value = it }
            )
        }
        Text(
            text = stringResource(Res.string.settings_reel_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
private fun SectionPlayInBackground(playInBgState: MutableState<Boolean>) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsLabel(Res.string.settings_background)
            Spacer(modifier = Modifier.weight(1f))
            MySwitch(
                enabled = { playInBgState.value },
                onToggle = { playInBgState.value = it }
            )
        }
        Text(
            text = stringResource(Res.string.settings_background_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
private fun Footer(
    enabled: () -> Boolean = { true },
    onStart: (ScreenMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isCompactDevice()) Text(
            text = stringResource(Res.string.recite_start),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MyButton(
                text = Res.string.recite_mode_normal,
                modifier = Modifier,//.weight(0.5f),
                onClick = { onStart(ScreenMode.Normal) },
                enabled = enabled
            )
            MyButton(
                text = Res.string.recite_mode_tv,
                modifier = Modifier,//.weight(0.5f),
                onClick = { onStart(ScreenMode.Tv) },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun MySwitch(
    enabled: () -> Boolean,
    onToggle: (Boolean) -> Unit
) {
    Switch(
        checked = enabled(),
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MaterialTheme.colorScheme.secondary,
            uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
            uncheckedTrackColor = Color.Transparent,
            uncheckedBorderColor = MaterialTheme.colorScheme.onPrimary
        ),
        onCheckedChange = onToggle
    )
}