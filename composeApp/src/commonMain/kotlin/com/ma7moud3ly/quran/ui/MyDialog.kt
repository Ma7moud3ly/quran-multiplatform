package com.ma7moud3ly.quran.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.background,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    usePlatformDefaultWidth: Boolean = isCompactDevice().not(),
    space: Dp = 16.dp,
    margin: Dp = 16.dp,
    cornerRadius: Dp = 8.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(space),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxSize().padding(margin),
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = usePlatformDefaultWidth
        )
    ) {
        Surface(color = background, shape = RoundedCornerShape(cornerRadius)) {
            Column(Modifier.fillMaxWidth()) {
                header()
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().then(modifier),
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    content = content
                )
                footer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMiniDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.background,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    usePlatformDefaultWidth: Boolean = isCompactDevice().not(),
    space: Dp = 16.dp,
    margin: Dp = 16.dp,
    cornerRadius: Dp = 8.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(space),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: @Composable () -> Unit = {},
    footer: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(margin),
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = usePlatformDefaultWidth
        )
    ) {
        Surface(color = background, shape = RoundedCornerShape(cornerRadius)) {
            Column(Modifier.fillMaxWidth()) {
                header()
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().then(modifier),
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    content = content
                )
                footer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogHeader(
    text: String,
    icon: ImageVector = Icons.Default.Close,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    textAlign: TextAlign = TextAlign.Center,
    onBack: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = textAlign,
                color = color,
                modifier = Modifier.weight(1f)
            )
            IconButton(onBack) {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    modifier = Modifier.size(28.dp),
                    tint = color
                )
            }
        }
        HorizontalDivider()
    }
}