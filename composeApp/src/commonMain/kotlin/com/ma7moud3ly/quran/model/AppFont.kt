package com.ma7moud3ly.quran.model

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.FontResource

data class AppFont(
    val id: String,
    val name: String,
    var fontSize: TextUnit = 30.sp,
    val letterSpacing: TextUnit = 0.sp,
    val lineHeight: TextUnit = 2.em,
    val fontType: FontResource
)
