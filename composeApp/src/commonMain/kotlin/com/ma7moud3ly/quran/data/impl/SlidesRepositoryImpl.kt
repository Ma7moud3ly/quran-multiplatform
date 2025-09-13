package com.ma7moud3ly.quran.data.impl

import com.ma7moud3ly.quran.data.repository.SlidesRepository
import com.ma7moud3ly.quran.model.TvSlide
import com.ma7moud3ly.quran.platform.PlaybackVideos
import org.koin.core.annotation.Single

@Single
class SlidesRepositoryImpl(
    private val playbackVideos: PlaybackVideos
) : SlidesRepository {


    override fun getSlides(): List<TvSlide> {
        return playbackVideos.slides
    }

    override fun getSlide(slideId: String): TvSlide? {
        return getSlides().firstOrNull() { it.id == slideId }
    }
}


