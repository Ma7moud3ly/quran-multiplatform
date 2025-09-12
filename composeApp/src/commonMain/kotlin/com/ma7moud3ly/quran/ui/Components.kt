package com.ma7moud3ly.quran.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.platform.Log
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs


@Composable
fun MySurface(
    cornerRadius: Dp = 16.dp,
    shape: RoundedCornerShape = RoundedCornerShape(cornerRadius),
    color: Color = MaterialTheme.colorScheme.surface,
    contentAlignment: Alignment = Alignment.Center,
    modifier: Modifier = Modifier,
    surfaceModifier: Modifier = Modifier,
    shadowElevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = color,
        modifier = surfaceModifier,
        shadowElevation = shadowElevation,
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
    ) {
        Box(
            modifier = modifier,
            contentAlignment = contentAlignment,
            content = content
        )
    }
}

@Composable
fun MySurfaceColumn(
    cornerRadius: Dp = 16.dp,
    shape: RoundedCornerShape = RoundedCornerShape(cornerRadius),
    color: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier.fillMaxWidth(),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    surfaceModifier: Modifier = Modifier,
    space: Dp = 16.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = color,
        modifier = surfaceModifier,
        border = border,
        shadowElevation = shadowElevation,
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(space),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}

@Composable
fun MySurfaceRow(
    cornerRadius: Dp = 16.dp,
    shape: RoundedCornerShape = RoundedCornerShape(cornerRadius),
    color: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier.fillMaxWidth(),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    surfaceModifier: Modifier = Modifier,
    space: Dp = 16.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(space),
    border: BorderStroke? = null,
    shadowElevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = color,
        modifier = surfaceModifier,
        border = border,
        shadowElevation = shadowElevation,
        enabled = onClick != null,
        onClick = { onClick?.invoke() },
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment,
            content = content
        )
    }
}


@Composable
fun SwipeableBox(
    modifier: Modifier = Modifier,
    verticalScroll: Boolean = false,
    contentAlignment: Alignment = Alignment.TopStart,
    onSwipeRight: () -> Unit = {},
    onSwipeLeft: () -> Unit = {},
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    var totalDragOffset by remember { mutableStateOf(Offset.Zero) }
    var currentDragOffset by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        totalDragOffset = Offset.Zero
                        currentDragOffset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        if (abs(dragAmount.x) > abs(dragAmount.y) * 1.5) {
                            change.consume()
                        }
                        totalDragOffset += dragAmount
                        currentDragOffset += dragAmount
                    },
                    onDragEnd = {
                        val swipeThresholdDp: Dp = 50.dp
                        val swipeThresholdPx = with(density) { swipeThresholdDp.toPx() }

                        val horizontalDrag = totalDragOffset.x
                        val verticalDrag = totalDragOffset.y

                        if (abs(horizontalDrag) > abs(verticalDrag)) {
                            // It's a horizontal swipe
                            if (horizontalDrag > swipeThresholdPx) {
                                Log.v("SwipeableBox", "Swiped Right")
                                onSwipeRight()
                            } else if (horizontalDrag < -swipeThresholdPx) {
                                Log.v("SwipeableBox", "Swiped Left")
                                onSwipeLeft()
                            } else {
                                Log.v("SwipeableBox", "Horizontal drag too short")
                            }
                        } else {
                            // It's a vertical swipe
                            if (verticalDrag > swipeThresholdPx) {
                                Log.v("SwipeableBox", "Swiped Down")
                                onSwipeDown()
                            } else if (verticalDrag < -swipeThresholdPx) {
                                Log.v("SwipeableBox", "Swiped Up")
                                onSwipeUp()
                            } else {
                                Log.v(
                                    "SwipeableBox",
                                    "Vertical drag too short, potentially a scroll attempt"
                                )
                                // If you have a scrollable child, it might have handled it.
                            }
                        }
                        currentDragOffset = Offset.Zero
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (verticalScroll)
                        Modifier.verticalScroll(rememberScrollState())
                    else Modifier
                ),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}


@Composable
fun MyButton(
    text: StringResource,
    modifier: Modifier = Modifier.fillMaxWidth(),
    color: Color = MaterialTheme.colorScheme.onSecondary,
    background: Color = MaterialTheme.colorScheme.secondary,
    enabled: () -> Boolean = { true },
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled(),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = color
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        BasicText(
            text = stringResource(text),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSecondary
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(11.sp, 16.sp)
        )
    }
}

@Composable
fun MyButtonSmall(
    text: StringResource,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSecondary,
    background: Color = MaterialTheme.colorScheme.secondary,
    enabled: () -> Boolean = { true },
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled(),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = color
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        BasicText(
            text = stringResource(text),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSecondary
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(10.sp, 14.sp)
        )
    }
}

@Composable
internal fun RoundButton(
    icon: DrawableResource,
    iconSize: Dp = 22.dp,
    iconPadding: Dp = 6.dp,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clip(CircleShape).clickable(onClick = onClick),
        shape = CircleShape,
        color = background,
        //onClick = onClick // adds extra padding 🤷
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.padding(iconPadding).size(iconSize)
        )
    }
}

@Composable
internal fun RoundButton(
    icon: ImageVector,
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 8.dp,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = background,
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.padding(iconPadding).size(iconSize)
        )
    }
}



@Composable
fun textSelectionColors(): TextSelectionColors {
    return TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
    )
}

@Composable
fun SelectionBox(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors()
    ) {
        SelectionContainer(content = content)
    }
}