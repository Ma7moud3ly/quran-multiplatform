package com.ma7moud3ly.quran.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable


@Composable
actual fun MyBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled, onBack)
}