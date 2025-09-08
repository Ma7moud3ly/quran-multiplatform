package com.ma7moud3ly.quran.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    background: Color = MaterialTheme.colorScheme.background,
    space: Dp = 16.dp,
    flyingContentAlignment: Alignment = Alignment.Center,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    flyingContent: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = background,
        topBar = topBar,
        snackbarHost = snackbarHost,
        bottomBar = bottomBar
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = flyingContentAlignment
        ) {
            flyingContent()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier),
                verticalArrangement = Arrangement.spacedBy(space),
                horizontalAlignment = horizontalAlignment,
                content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHeader(
    text: String,
    icon: ImageVector = Icons.AutoMirrored.Default.ArrowBack,
    background: Color = MaterialTheme.colorScheme.background,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    textAlign: TextAlign = TextAlign.Start,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showDivider: Boolean = true,
    onBack: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = background,
                    scrolledContainerColor = background
                ),
                title = {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        color = color,
                        textAlign = textAlign
                    )
                },
                navigationIcon = {
                    IconButton(onBack) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "",
                            modifier = Modifier.size(28.dp),
                            tint = color
                        )
                    }
                }
            )
            if (showDivider) HorizontalDivider()
        }
    }
}

