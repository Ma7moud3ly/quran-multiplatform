package com.ma7moud3ly.quran.features.recitation.download


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ma7moud3ly.quran.model.DownloadProgress
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.model.testRecitation
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MyButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.download_audio_quality
import quran.composeapp.generated.resources.download_cancel
import quran.composeapp.generated.resources.download_size
import quran.composeapp.generated.resources.download_size_mb
import quran.composeapp.generated.resources.recite_chapter
import quran.composeapp.generated.resources.reciter

private const val TAG = "DownloadProgressDialog"

@Preview
@Composable
private fun DownloadDialogPreview() {
    var progress by remember { mutableStateOf(DownloadProgress()) }
    LaunchedEffect(Unit) {
        for (i in 1..100) {
            delay(1000)
            val downloaded = 1.0 * i
            val size = 30.0
            //val percent = "%.2f".format(downloaded / size * 100).toFloatOrNull() ?: 0f
            val percent = (downloaded / size * 100).toFloat()
            progress = DownloadProgress(
                downloaded = downloaded,
                size = size,
                percent = percent
            )
        }
    }

    AppTheme(darkTheme = true) {
        DownloadDialogContent(
            recitation = testRecitation,
            downloadProgress = { progress },
            onStopDownload = {}
        )
    }
}

@Preview
@Composable
private fun DownloadDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        DownloadDialogContent(
            recitation = testRecitation,
            downloadProgress = {
                DownloadProgress(
                    downloaded = 1.0,
                    size = 30.0,
                    percent = 0.33f
                )
            },
            onStopDownload = {}
        )
    }
}


@Composable
fun DownloadDialogContent(
    recitation: Recitation,
    downloadProgress: () -> DownloadProgress,
    onStopDownload: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        val coroutineScope = rememberCoroutineScope()
        var progressJob by remember { mutableStateOf<Job?>(null) }
        val progress = downloadProgress()
        var progressPercent by remember(progress.percent) { mutableStateOf(progress.percent) }
        val totalSize by remember(progress.size) { mutableStateOf<Double>(progress.size) }

        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                LinearProgressIndicator(
                    progress = { progressPercent },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFA000),
                    trackColor = MaterialTheme.colorScheme.secondary,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {


                    ItemDetails(
                        title = Res.string.recite_chapter,
                        value = recitation.chapter.name
                    )
                    val reciter by remember(recitation) { recitation.reciterState }
                    ItemDetails(
                        title = Res.string.reciter,
                        value = reciter.name
                    )

                    ItemDetails(
                        title = Res.string.download_audio_quality,
                        value = reciter.downloadQuality
                    )

                    if (totalSize != 0.0) ItemDetails(
                        title = Res.string.download_size,
                        value = stringResource(
                            Res.string.download_size_mb,
                            totalSize
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    MyButton(
                        text = Res.string.download_cancel,
                        onClick = onStopDownload

                    )
                }
            }
        }

        LaunchedEffect(totalSize) {
            if (totalSize == 0.0 && progressJob == null) {
                progressJob = coroutineScope.launch {
                    while (true) {
                        for (i in 0..10) {
                            progressPercent = i / 10f
                            delay(100)
                        }
                        delay(200)
                    }
                }
            } else {
                progressJob?.cancel()
                progressJob = null
            }
        }
    }
}

@Composable
internal fun ItemDetails(
    title: StringResource,
    value: Any
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
    }
}

