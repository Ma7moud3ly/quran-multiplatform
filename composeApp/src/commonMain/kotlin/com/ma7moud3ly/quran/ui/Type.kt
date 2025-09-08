package com.ma7moud3ly.quran.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.platform.getPlatform
import com.ma7moud3ly.quran.platform.isWasmJs
import org.jetbrains.compose.resources.Font
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.arabic_islamic_quran
import quran.composeapp.generated.resources.basmeallah2
import quran.composeapp.generated.resources.cairo_black
import quran.composeapp.generated.resources.cairo_bold
import quran.composeapp.generated.resources.cairo_light
import quran.composeapp.generated.resources.cairo_medium
import quran.composeapp.generated.resources.cairo_regular
import quran.composeapp.generated.resources.cairo_thin
import quran.composeapp.generated.resources.hafs_smart_regular
import quran.composeapp.generated.resources.noto_kufi_black
import quran.composeapp.generated.resources.noto_kufi_bold
import quran.composeapp.generated.resources.noto_kufi_light
import quran.composeapp.generated.resources.noto_kufi_medium
import quran.composeapp.generated.resources.noto_kufi_regular
import quran.composeapp.generated.resources.noto_kufi_thin
import quran.composeapp.generated.resources.surah_name_v4
import quran.composeapp.generated.resources.uthmanic_hafs_bold

@Composable
fun notoKufiFontFamily() = FontFamily(
    Font(Res.font.noto_kufi_thin, FontWeight.ExtraLight),
    Font(Res.font.noto_kufi_light, FontWeight.Light),
    Font(Res.font.noto_kufi_regular, FontWeight.Normal),
    Font(Res.font.noto_kufi_medium, FontWeight.Medium),
    Font(Res.font.noto_kufi_bold, FontWeight.Bold),
    Font(Res.font.noto_kufi_black, FontWeight.Black),
)

@Composable
fun cairoFontFamily() = FontFamily(
    Font(Res.font.cairo_thin, FontWeight.ExtraLight),
    Font(Res.font.cairo_light, FontWeight.Light),
    Font(Res.font.cairo_regular, FontWeight.Normal),
    Font(Res.font.cairo_medium, FontWeight.Medium),
    Font(Res.font.cairo_bold, FontWeight.Bold),
    Font(Res.font.cairo_black, FontWeight.Black),
)

@Composable
fun basmeallahFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.basmeallah2, FontWeight.Normal)
    )
}

@Composable
fun arabicIslamicFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.arabic_islamic_quran, FontWeight.Normal)
    )
}

@Composable
fun suraNameFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.surah_name_v4, FontWeight.Normal)
    )
}


@Composable
fun hafsSmartFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.hafs_smart_regular, FontWeight.Normal),
        Font(Res.font.hafs_smart_regular, FontWeight.Bold),
    )
}

@Composable
fun hafsBoldFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.uthmanic_hafs_bold, FontWeight.Normal)
    )
}

@Composable
private fun appFontFamily() =
    if (getPlatform().isWasmJs) cairoFontFamily() else notoKufiFontFamily()

@Composable
fun appTypography(): Typography {
    return with(MaterialTheme.typography) {
        copy(
            headlineLarge = headlineLarge.copy(fontFamily = appFontFamily()),
            headlineMedium = headlineMedium.copy(fontFamily = appFontFamily()),
            headlineSmall = headlineSmall.copy(fontFamily = appFontFamily()),
            titleLarge = titleLarge.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 28.sp
            ), titleMedium = titleMedium.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 24.sp
            ),
            titleSmall = titleSmall.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),
            bodyLarge = bodyLarge.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            bodyMedium = bodyMedium.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            bodySmall = bodySmall.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            labelLarge = labelLarge.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            labelMedium = labelMedium.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            labelSmall = labelSmall.copy(
                fontFamily = appFontFamily(),
                fontWeight = FontWeight.ExtraLight,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        )
    }
}

