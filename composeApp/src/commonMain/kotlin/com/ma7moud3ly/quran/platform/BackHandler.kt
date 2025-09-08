package com.ma7moud3ly.quran.platform

import androidx.compose.runtime.Composable

@Composable
expect fun MyBackHandler(enabled: Boolean = true, onBack: () -> Unit)