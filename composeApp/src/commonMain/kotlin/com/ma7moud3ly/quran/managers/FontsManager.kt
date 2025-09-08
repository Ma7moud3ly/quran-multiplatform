package com.ma7moud3ly.quran.managers

import com.ma7moud3ly.quran.model.AppFont
import org.koin.core.annotation.Single
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.amiri_quran_colored
import quran.composeapp.generated.resources.elgharib_noon_hafs
import quran.composeapp.generated.resources.hafs_smart_regular
import quran.composeapp.generated.resources.indopak
import quran.composeapp.generated.resources.uthmanic_hafs_bold
import quran.composeapp.generated.resources.uthmanic_hafs_v22

/**
 * Manages available fonts within the application.
 *
 * Provides a centralized list of `AppFont` objects, each with an ID, name,
 * and font resource. Includes a default font and methods to retrieve all fonts
 * or a specific font by ID.
 */
@Single
class FontsManager() {
    companion object {
        private val fonts = listOf(
            AppFont(
                id = "hafs_bold_2020",
                name = "خط عثماني عريض - حفص",
                fontType = Res.font.uthmanic_hafs_bold
            ), AppFont(
                id = "hafs_v22",
                name = "خط عثماني - حفص ",
                fontType = Res.font.uthmanic_hafs_v22
            ),
            AppFont(
                id = "hafs_smart",
                name = "خط حفص الذكي",
                fontType = Res.font.hafs_smart_regular
            ),
            AppFont(
                id = "amiri_colored",
                name = "خط الأميرى الملون",
                fontType = Res.font.amiri_quran_colored,
                fixedLineHeight = false
            ),
            AppFont(
                id = "elgharib_noon",
                name = "خط الغريب نون - حفص",
                fontType = Res.font.elgharib_noon_hafs
            ),
            AppFont(
                id = "indopak",
                name = "خط الإندوباك",
                fontType = Res.font.indopak
            )
        )

        const val DEFAULT_FONT = "hafs_bold_2020"
        fun first() = fonts.firstOrNull { it.id == DEFAULT_FONT } ?: fonts.first()
    }

    fun getFonts(): List<AppFont> = fonts
    fun getFont(id: String) = fonts.firstOrNull { it.id == id } ?: fonts.first()
}