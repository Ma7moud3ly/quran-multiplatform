package com.ma7moud3ly.quran.features.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.quran.features.home.Logo
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MyAlertDialog
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.ma7moud3ly.quran.resources.Res
import com.ma7moud3ly.quran.resources.app_link_facebook
import com.ma7moud3ly.quran.resources.app_link_github
import com.ma7moud3ly.quran.resources.app_link_instagram
import com.ma7moud3ly.quran.resources.app_name
import com.ma7moud3ly.quran.resources.app_version
import com.ma7moud3ly.quran.resources.facebook
import com.ma7moud3ly.quran.resources.github
import com.ma7moud3ly.quran.resources.instagram

@Preview
@Composable
private fun AboutAppDialogPreview() {
    AppTheme {
        AboutAppDialog(onDismiss = {})
    }
}

@Preview
@Composable
private fun AboutAppDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        AboutAppDialog(onDismiss = {})
    }
}

@Composable
internal fun AboutAppDialog(
    onDismiss: () -> Unit
) {
    MyAlertDialog(
        onDismissRequest = onDismiss,
        cornerRadius = 24.dp
    ) {
        // App Icon + Name
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Logo(size = 70.dp, animated = false)
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.app_version),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val uriHandler = LocalUriHandler.current
        val instagram = stringResource(Res.string.app_link_instagram)
        val facebook = stringResource(Res.string.app_link_facebook)
        val github = stringResource(Res.string.app_link_github)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { uriHandler.openUri(instagram) }) {
                SocialMediaIcon(Res.drawable.instagram)
            }
            IconButton(onClick = { uriHandler.openUri(facebook) }) {
                SocialMediaIcon(Res.drawable.facebook, size = 36.dp)
            }
            IconButton(onClick = { uriHandler.openUri(github) }) {
                SocialMediaIcon(Res.drawable.github)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SocialMediaIcon(
    icon: DrawableResource,
    size: Dp = 32.dp
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = "",
        tint = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.size(size)
    )
}