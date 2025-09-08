package com.ma7moud3ly.quran.data.impl

import com.ma7moud3ly.quran.data.repository.SlidesRepository
import com.ma7moud3ly.quran.managers.SlidesManager
import com.ma7moud3ly.quran.model.TvSlide
import org.koin.core.annotation.Single

@Single
class SlidesRepositoryImpl(
    private val slidesManager: SlidesManager
) : SlidesRepository {


    override  fun getSlides(): List<TvSlide> {
        return slidesManager.slides
    }

    override  fun getSlide(slideId: String): TvSlide? {
        return getSlides().firstOrNull() { it.id == slideId }
    }
}


