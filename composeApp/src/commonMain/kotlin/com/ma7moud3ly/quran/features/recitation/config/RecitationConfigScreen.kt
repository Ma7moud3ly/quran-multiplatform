package com.ma7moud3ly.quran.features.recitation.config

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.ma7moud3ly.quran.model.ScreenMode
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.platform.rememberNotificationsPermissionsState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.recite_permission_required

private const val TAG = "RecitationConfigScreen"

@Composable
fun RecitationConfigScreen(
    viewModel: RecitationViewModel,
    canChangeChapter: Boolean,
    canChangeReciter: Boolean,
    canChangeVerse: Boolean,
    recitationEvents: (RecitationEvents) -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val chapter by viewModel.chapterFlow.collectAsState()
    val reciters = remember { viewModel.reciters }
    val permissionsState = rememberNotificationsPermissionsState()
    val downloadedChapters = remember { viewModel.downloadedChapters }
    val recitationState by remember { viewModel.recitationState }
    val message = stringResource(Res.string.recite_permission_required)

    LaunchedEffect(Unit) {
        recitationState.setContentLock(
            canChangeChapter = canChangeChapter,
            canChangeReciter = canChangeReciter,
            canChangeVerse = canChangeVerse
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveLastReciters()
        }
    }

    fun initRecitation(screenMode: ScreenMode) {
        val chapter = chapter ?: return
        val lastVerse = recitationState.getLastVerse()
        val recitation = Recitation(
            chapter = chapter.copy(verses = chapter.verses.subList(0, lastVerse)),
            reciters = ArrayList(reciters),
            screenMode = screenMode,
            firstVerse = recitationState.getFirstVerse(),
            lastVerse = lastVerse,
            reelMode = recitationState.getReelMode(),
            playInBackground = recitationState.getPlayInBackground(),
            playLocally = true,
            playbackMode = recitationState.getPlaybackMode()
        )

        coroutineScope.launch {
            val event = if (viewModel.platformSupportDownloading()) {
                if (recitationState.isMultipleReciterMode()) {
                    RecitationEvents.StartOnline
                } else if (viewModel.isFullyDownloaded(recitation)) {
                    RecitationEvents.StartLocally
                } else if (viewModel.isFullyCachedCached(recitation)) {
                    RecitationEvents.StartOnline
                } else {
                    RecitationEvents.ConfirmDownload
                }
            } else {
                RecitationEvents.StartOnline
            }

            if (event.playOnline) recitation.setOnlineDatasource()
            viewModel.setRecitation(recitation)
            recitationEvents(event)
        }
    }

    fun checkNotificationsPermission(mode: ScreenMode) {
        if (recitationState.getPlayInBackground().not()) {
            initRecitation(mode)
        } else {
            if (permissionsState.isGranted()) initRecitation(mode)
            else permissionsState.request { granted ->
                if (granted) initRecitation(mode)
                else coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    RecitationConfigScreenContent(
        snackbarHostState = snackbarHostState,
        downloadedChapters = {
            if (recitationState.isSingleReciterMode()) downloadedChapters
            else emptyList()
        },
        selectedChapter = { chapter },
        reciters = { reciters },
        recitationState = { recitationState },
        uiEvents = {
            when (it) {
                is ConfigEvents.OnBack -> {
                    recitationEvents(RecitationEvents.OnBack)
                }

                is ConfigEvents.PickChapters -> {
                    recitationEvents(RecitationEvents.ChaptersDialog(chapter?.id))
                }

                is ConfigEvents.PickReciters -> {
                    val event = RecitationEvents.RecitersDialog(
                        selectMultiple = recitationState.isMultipleReciterMode()
                    )
                    recitationEvents(event)
                }

                is ConfigEvents.RemoveReciter -> {
                    viewModel.removeReciter(it.reciter)
                }

                is ConfigEvents.ClearReciters -> {
                    viewModel.clearReciters()
                }

                is ConfigEvents.SelectChapter -> {
                    viewModel.getChapter(it.chapter.id)
                }

                is ConfigEvents.InitRecitation -> {
                    checkNotificationsPermission(it.mode)
                }
            }
        }
    )
}