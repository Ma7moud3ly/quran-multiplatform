package com.ma7moud3ly.quran.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection


private val lightColors = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color(0xFF1877F2),
    onSecondary = Color.White,
    tertiary = Color(0xFF999999),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF8F8F8),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    onSurface = Color.Black,
    error = Color(0xFFD32F2F)
)

private val darkColors = darkColorScheme(
    primary = Color(0xFF2C2C30),
    onPrimary = Color.White,
    secondary = Color(0xFF1877F2),
    onSecondary = Color.White,
    tertiary = Color(0xFF666666),
    background = Color(0xFF2C2C30),
    onBackground = Color.White,
    surface = Color(0x804C4C4E),
    surfaceContainerHigh = Color(0x804C4C4E),
    onSurface = Color.White,
    error = Color(0xFFD32F2F)
)

fun themeColors(darkTheme: Boolean = true): ColorScheme {
    return if (darkTheme) darkColors else lightColors
}

@Composable
fun AppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = themeColors(darkTheme),
            typography = appTypography(),
            content = content
        )
    }
}
