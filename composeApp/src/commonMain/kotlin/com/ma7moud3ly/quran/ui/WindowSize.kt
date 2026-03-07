package com.ma7moud3ly.quran.ui

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Composable
fun isCompactDevice(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
}

@Composable
fun isMediumDevice(): Boolean {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
}
