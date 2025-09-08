package com.ma7moud3ly.quran.data.repository

import com.ma7moud3ly.quran.model.TvSlide

/**
 * Interface defining the contract for accessing and managing TV slides.
 * This repository provides methods to retrieve all slides or a specific slide by its ID.
 */
interface SlidesRepository {
    fun getSlides(): List<TvSlide>
    fun getSlide(slideId: String): TvSlide?
}