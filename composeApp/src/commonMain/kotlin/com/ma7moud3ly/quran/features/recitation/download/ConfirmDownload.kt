package com.ma7moud3ly.quran.features.recitation.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.model.testRecitation
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.DialogHeader
import com.ma7moud3ly.quran.ui.MyButton
import com.ma7moud3ly.quran.ui.MyMiniDialog
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.download
import quran.composeapp.generated.resources.download_audio_quality
import quran.composeapp.generated.resources.download_deny
import quran.composeapp.generated.resources.download_deny_message
import quran.composeapp.generated.resources.download_message
import quran.composeapp.generated.resources.ok
import quran.composeapp.generated.resources.recite_chapter
import quran.composeapp.generated.resources.reciter


@Preview
@Composable
private fun ConfirmDownloadDialogPreview() {
    AppTheme(darkTheme = true) {
        ConfirmDownloadDialogContent(
            recitation = testRecitation,
            onDownload = {},
            onPlayOnline = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun ConfirmDownloadDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        ConfirmDownloadDialogContent(
            recitation = testRecitation,
            onDownload = {},
            onPlayOnline = {},
            onDismiss = {}
        )
    }
}

@Composable
fun ConfirmDownloadDialog(
    recitation: Recitation?,
    onDownload: () -> Unit,
    onPlayOnline: () -> Unit,
    onDismiss: () -> Unit
) {
    if (recitation != null) {
        ConfirmDownloadDialogContent(
            recitation = recitation,
            onDownload = onDownload,
            onPlayOnline = onPlayOnline,
            onDismiss = onDismiss
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmDownloadDialogContent(
    recitation: Recitation,
    onDownload: () -> Unit,
    onPlayOnline: () -> Unit,
    onDismiss: () -> Unit
) {
    val reciter by remember(recitation) { recitation.reciterState }

    MyMiniDialog(
        onDismissRequest = onDismiss,
        space = 8.dp,
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        header = {
            DialogHeader(
                text = stringResource(Res.string.download),
                onBack = onDismiss
            )
        }
    ) {
        Text(
            text = stringResource(Res.string.download_message),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(Modifier.height(8.dp))
        ItemDetails(
            title = Res.string.recite_chapter,
            value = recitation.chapter.name
        )
        ItemDetails(
            title = Res.string.reciter,
            value = reciter.name
        )
        ItemDetails(
            title = Res.string.download_audio_quality,
            value = reciter.downloadQuality
        )

        if (reciter.canListen) Text(
            text = stringResource(Res.string.download_deny_message),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MyButton(
                text = Res.string.ok,
                onClick = onDownload,
                modifier = Modifier.weight(0.5f)
            )
            if (reciter.canListen) MyButton(
                text = Res.string.download_deny,
                onClick = onPlayOnline,
                color = MaterialTheme.colorScheme.onPrimary,
                background = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(0.5f)
            )
        }
    }
}