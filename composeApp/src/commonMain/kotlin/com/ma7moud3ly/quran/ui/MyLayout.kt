package com.ma7moud3ly.quran.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun LayoutRtl(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalLayoutDirection provides LayoutDirection.Rtl,
        content = content
    )
}

@Composable
fun LayoutLtr(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalLayoutDirection provides LayoutDirection.Ltr,
        content = content
    )
}