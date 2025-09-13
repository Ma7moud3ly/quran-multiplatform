package com.ma7moud3ly.quran.platform

import com.ma7moud3ly.quran.model.TvSlide

interface PlaybackVideos {
    val slides: List<TvSlide>
}

expect fun getPlaybackVideos(): PlaybackVideos
