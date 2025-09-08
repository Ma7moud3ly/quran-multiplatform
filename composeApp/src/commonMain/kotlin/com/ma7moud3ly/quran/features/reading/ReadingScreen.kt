package com.ma7moud3ly.quran.features.reading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReadingScreen(
    viewModel: ReadingViewModel = koinViewModel(),
    onSelectChapter: (Int) -> Unit,
    onPlayVerse: (Int, Int) -> Unit,
    onSettings: () -> Unit,
    onBack: () -> Unit
) {
    val chapter by viewModel.chapterFlow.collectAsState()
    val versesManager by remember { viewModel.versesManager }
    val settings by viewModel.readingSettingFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LifecycleResumeEffect(LocalLifecycleOwner) {
        viewModel.keepScreenOn(true)
        onPauseOrDispose {
            viewModel.keepScreenOn(false)
        }
    }

    DisposableEffect(LocalLifecycleOwner) {
        onDispose {
            coroutineScope.launch(NonCancellable) {
                delay(200)
                viewModel.saveHistory()
            }
        }
    }

    if (chapter != null && versesManager != null) {
        val chapter = chapter ?: return
        ReadingScreenContent(
            chapter = chapter,
            versesManager = versesManager!!,
            appSettings = { settings },
            uiEvents = {
                when (it) {
                    is ReadingEvents.Back -> onBack()
                    is ReadingEvents.OpenSettings -> onSettings()
                    is ReadingEvents.NextChapter -> onSelectChapter(chapter.id + 1)
                    is ReadingEvents.PlayVerse -> onPlayVerse(chapter.id, it.verseId)
                    is ReadingEvents.PreviousChapter -> onSelectChapter(chapter.id - 1)
                }
            }
        )
    }
}