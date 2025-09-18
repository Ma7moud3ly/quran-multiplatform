package com.ma7moud3ly.quran.features.home.reciters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.rememberAsyncImagePainter
import com.ma7moud3ly.quran.model.Reciter
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.MySurface
import com.ma7moud3ly.quran.ui.RoundButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.close
import quran.composeapp.generated.resources.icon

val testReciters = listOf(
    Reciter(
        id = "1",
        name = "أبو بكر الشاطري",
    ),
    Reciter(
        id = "2",
        name = "أحمد بن علي العجمي",
    ),
    Reciter(
        id = "3",
        name = "توفيق الصايغ",
    ),
    Reciter(
        id = "4",
        name = "سعد الغامدي",
    ),
    Reciter(
        id = "5",
        name = "سعود الشريم",
    ),
)

@Preview
@Composable
private fun RecitersPagePreview() {
    AppTheme {
        RecitersPage(
            list = testReciters,
            onOpenReciter = {}
        )
    }
}

@Preview
@Composable
private fun RecitersPagePreviewLight() {
    AppTheme(darkTheme = false) {
        RecitersPage(
            list = testReciters,
            onOpenReciter = {}
        )
    }
}

@Composable
internal fun RecitersPage(
    list: List<Reciter>,
    onOpenReciter: (Reciter) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        columns = GridCells.Adaptive(160.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(list) { reciter ->
            ItemReciter(
                name = reciter.name,
                imageUrl = reciter.imageUrl,
                modifier = Modifier.aspectRatio(0.9f),
                onClick = { onOpenReciter(reciter) }
            )
        }
    }
}

@Composable
fun ItemReciter(
    modifier: Modifier,
    name: String,
    imageUrl: String,
    showRemove: Boolean = false,
    onRemove: () -> Unit = {},
    onClick: () -> Unit,
) {
    MySurface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color.White),
        surfaceModifier = modifier,
        modifier = Modifier.fillMaxSize(),
        onClick = onClick
    ) {
        if (showRemove) RoundButton(
            icon = Res.drawable.close,
            iconSize = 18.dp,
            iconPadding = 3.dp,
            background = Color.White,
            color = MaterialTheme.colorScheme.error,
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .zIndex(2f)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl,
                    placeholder = painterResource(Res.drawable.icon),
                    error = painterResource(Res.drawable.icon)
                ),
                contentDescription = name,
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentScale = ContentScale.FillWidth
            )
            BasicText(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                // overflow = TextOverflow.StartEllipsis,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 8.sp,
                    maxFontSize = 11.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }
    }
}
