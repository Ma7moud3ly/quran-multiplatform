package com.ma7moud3ly.quran.platform

import com.ma7moud3ly.quran.model.TvBackground

interface BackgroundVideos {
    val backgrounds: List<TvBackground>
}

expect fun getPlaybackVideos(): BackgroundVideos
