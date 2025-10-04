package com.ma7moud3ly.quran.features.recitation.config.background

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ma7moud3ly.quran.model.TvBackground
import com.ma7moud3ly.quran.model.testBackgroundsManager
import com.ma7moud3ly.quran.platform.VideoPlayer
import com.ma7moud3ly.quran.platform.rememberVideoPlayerState
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MySurface
import com.ma7moud3ly.quran.ui.RoundButton
import com.ma7moud3ly.quran.ui.ScreenHeader
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.close
import quran.composeapp.generated.resources.settings_tv_bg
import quran.composeapp.generated.resources.settings_tv_bg_add

@Preview
@Composable
private fun BackgroundContentPreview() {
    AppTheme(darkTheme = true) {
        TvBackgroundContent(
            backgrounds = testBackgroundsManager.backgrounds,
            selectedBackground = { TvBackground() },
            onSelectBackground = {},
            onRemoveBackground = {},
            onAddNewBackground = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun BackgroundContentPreviewLight() {
    AppTheme(darkTheme = false) {
        TvBackgroundContent(
            backgrounds = testBackgroundsManager.backgrounds,
            selectedBackground = { TvBackground() },
            onSelectBackground = {},
            onRemoveBackground = {},
            onAddNewBackground = {},
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TvBackgroundContent(
    backgrounds: List<TvBackground>,
    selectedBackground: () -> TvBackground?,
    onSelectBackground: (TvBackground) -> Unit,
    onRemoveBackground: (TvBackground) -> Unit,
    onAddNewBackground: () -> Unit,
    onDismiss: () -> Unit,
) {
    val lazyVerticalGridState = rememberLazyGridState()
    val selectedBackground = selectedBackground()

    LaunchedEffect(backgrounds) {
        if (backgrounds.isNotEmpty()) {
            val index = backgrounds.indexOf(selectedBackground)
            if (index != -1) {
                lazyVerticalGridState.animateScrollToItem(index)
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { newValue ->
                // Prevent the sheet from hiding when user tries to swipe down
                newValue != SheetValue.Hidden
            }
        ),
        shape = BottomSheetDefaults.HiddenShape,
        dragHandle = {
            ScreenHeader(
                text = stringResource(Res.string.settings_tv_bg),
                textAlign = TextAlign.Center,
                icon = Icons.Default.Close,
                onBack = onDismiss
            )
        },
        onDismissRequest = onDismiss
    ) {
        ElevatedButton(
            onClick = onAddNewBackground,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.settings_tv_bg_add),
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = "",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        LazyVerticalGrid(
            state = lazyVerticalGridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            columns = GridCells.Adaptive(120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(backgrounds) { tvBackground ->
                ItemBackground(
                    tvBackground = tvBackground,
                    static = true,
                    modifier = Modifier.aspectRatio(0.9f),
                    border = BorderStroke(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    selected = { selectedBackground?.id == tvBackground.id },
                    onRemove = { onRemoveBackground(tvBackground) },
                    onClick = { onSelectBackground(tvBackground) }
                )
            }
        }
    }
}

@Composable
internal fun ItemBackground(
    modifier: Modifier = Modifier,
    tvBackground: TvBackground,
    static: Boolean = false,
    border: BorderStroke? = null,
    selected: () -> Boolean = { true },
    onRemove: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val showThumbnail = static || LocalInspectionMode.current

    MySurface(
        surfaceModifier = modifier,
        onClick = onClick,
        border = if (selected()) border else null,
        shape = RoundedCornerShape(8.dp),
    ) {
        if (tvBackground.canRemove && onRemove != null) RoundButton(
            icon = Res.drawable.close,
            iconSize = 18.dp,
            iconPadding = 3.dp,
            background = Color.White,
            color = MaterialTheme.colorScheme.error,
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .zIndex(2f)
        )
        if (showThumbnail) Image(
            painter = tvBackground.video.getPainter(),
            modifier = modifier.fillMaxSize(),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
        else VideoPlayer(
            modifier = modifier.fillMaxSize(),
            state = rememberVideoPlayerState(),
            video = { tvBackground.video }
        )
    }
}