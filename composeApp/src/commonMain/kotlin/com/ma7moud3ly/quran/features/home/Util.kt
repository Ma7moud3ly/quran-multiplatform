package com.ma7moud3ly.quran.features.home

import org.jetbrains.compose.resources.StringResource
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.home_bookmarks
import quran.composeapp.generated.resources.home_index
import quran.composeapp.generated.resources.home_reciters

enum class HomeTab(
    val title: StringResource,
    val index: Int
) {
    ChaptersIndex(Res.string.home_index, 0),
    Reciters(Res.string.home_reciters, 1),
    Bookmarks(Res.string.home_bookmarks, 2)
}


