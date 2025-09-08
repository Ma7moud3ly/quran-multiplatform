package com.ma7moud3ly.quran.features.recitation.download

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ma7moud3ly.quran.model.DownloadProgress
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.platform.Log
import com.ma7moud3ly.quran.platform.platformKeepScreenOn
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "DownloadDialog"

@Composable
fun DownloadDialog(
    viewModel: DownloadsViewModel = koinViewModel(),
    recitation: Recitation?,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {

    val progress by viewModel.downloadProgress.collectAsState(DownloadProgress())

    LaunchedEffect(Unit) {
        if (recitation != null) {
            viewModel.downloadChapter(recitation)
        }
    }

    LifecycleResumeEffect(LocalLifecycleOwner) {
        platformKeepScreenOn(true)
        onPauseOrDispose { platformKeepScreenOn(false) }
    }


    LaunchedEffect(Unit) {
        viewModel.downloadComplete.collect { download ->
            if (recitation?.downloadDirectory != download.id) return@collect
            Log.v(
                TAG,
                "downloadId = ${download.id} - recitation.id = ${recitation.downloadDirectory}"
            )
            delay(1000)
            if (download.success) {
                Log.v(TAG, "Completed")
                onSuccess()
            } else {
                onDismiss()
                Log.v(TAG, "Failed")
            }

        }
    }

    if (recitation != null) {
        DownloadDialogContent(
            recitation = recitation,
            downloadProgress = { progress },
            onStopDownload = onDismiss
        )
    }
}
