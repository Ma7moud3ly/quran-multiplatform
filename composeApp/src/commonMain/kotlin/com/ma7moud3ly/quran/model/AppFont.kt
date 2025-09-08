package com.ma7moud3ly.quran.model

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.FontResource

data class AppFont(
    val id: String,
    val name: String,
    var fontSize: TextUnit = 30.sp,
    val fontType: FontResource,
    val fixedLineHeight: Boolean = true,
) {
    fun getLineHeight(): TextUnit {
        val size = fontSize.value
        return if (fixedLineHeight) (size * 1.5f).sp
        else {
            if (size > 80) (size * 4f).sp
            else if (size > 60) (size * 3.5f).sp
            else if (size > 50) (size * 3f).sp
            else if (size > 30) (size * 2.5f).sp
            else if (size > 25) (size * 2.3f).sp
            else (size * 2f).sp
        }
    }
}
