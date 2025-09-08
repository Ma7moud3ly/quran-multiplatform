package com.ma7moud3ly.quran.managers

import com.ma7moud3ly.quran.model.TvSlide

interface SlidesManager {
    val slides: List<TvSlide>
}

expect fun getSlidesManager(): SlidesManager

