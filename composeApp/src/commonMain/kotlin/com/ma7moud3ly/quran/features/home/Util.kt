package com.ma7moud3ly.quran.features.home

import org.jetbrains.compose.resources.StringResource
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.home_history
import quran.composeapp.generated.resources.home_index
import quran.composeapp.generated.resources.home_reciters

sealed class HomeTab(
    val title: StringResource,
    val index: Int
) {
    data object ChaptersIndex : HomeTab(Res.string.home_index, 0)
    data object Reciters : HomeTab(Res.string.home_reciters, 1)
    data object History : HomeTab(Res.string.home_history, 2)
}

val homeTabs: List<HomeTab> = listOf(
    HomeTab.ChaptersIndex,
    HomeTab.Reciters,
    HomeTab.History
)



