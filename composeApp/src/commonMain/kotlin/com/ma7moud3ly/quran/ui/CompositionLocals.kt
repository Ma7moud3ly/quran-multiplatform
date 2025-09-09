package com.ma7moud3ly.quran.ui

import androidx.compose.runtime.compositionLocalOf
import com.ma7moud3ly.quran.platform.getPlatform


val LocalPlatform = compositionLocalOf { getPlatform() }