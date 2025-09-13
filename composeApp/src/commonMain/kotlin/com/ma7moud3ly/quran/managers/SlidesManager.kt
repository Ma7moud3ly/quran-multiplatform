package com.ma7moud3ly.quran.managers

import androidx.compose.runtime.mutableStateOf
import com.ma7moud3ly.quran.data.repository.SettingsRepository
import com.ma7moud3ly.quran.data.repository.SlidesRepository
import com.ma7moud3ly.quran.model.SlideControls
import com.ma7moud3ly.quran.model.TvSlide
import org.koin.core.annotation.Single

/**
 * Manages the display and navigation of TV slides.
 *
 * This class is responsible for loading slides, handling slide selection,
 * and managing the visibility of slide controls.
 *
 * @property slidesRepository The repository for accessing slide data.
 * @property settingsRepository The repository for accessing application settings.
 */
@Single
class SlidesManager(
    private val slidesRepository: SlidesRepository,
    private val settingsRepository: SettingsRepository
) {
    /** A list of all available TV slides. */
    val slides: List<TvSlide> = slidesRepository.getSlides()

    /** The currently selected TV slide. */
    val selectedSlide = mutableStateOf(slides.first())

    /** The current state of the slide controls (e.g., show controls, show reciter). */
    val slideControls = mutableStateOf<SlideControls>(SlideControls.ShowControls)

    /** The index of the currently selected slide in the [slides] list. */
    private var selectedSlideIndex = 0

    init {
        val slideId = settingsRepository.tvSlide
        val initialSlide = slidesRepository.getSlide(slideId) ?: slides.first()
        selectedSlide.value = initialSlide
        selectedSlideIndex = slides.indexOf(initialSlide)
    }


    /**
     * Saves the ID of the currently selected slide to the settings.
     */
    private fun saveSlide() {
        val slideId = selectedSlide.value.id
        settingsRepository.setTvSlide(slideId)
    }

    /**
     * Selects a specific slide to be displayed.
     *
     * @param slide The [TvSlide] to select.
     */
    fun selectSlide(slide: TvSlide) {
        selectedSlide.value = slide
        saveSlide()
    }

    /**
     * Navigates to the next slide in the list.
     * If the current slide is the last one, it wraps around to the first slide.
     *
     * @param onSlideChanged A callback function that is invoked with the index of the new slide.
     */
    fun nextSlide(onSlideChanged: (slideIndex: Int) -> Unit) {
        if (selectedSlideIndex < slides.size - 1) selectedSlideIndex++
        else selectedSlideIndex = 0
        selectedSlide.value = slides[selectedSlideIndex]
        onSlideChanged(selectedSlideIndex)
        saveSlide()
    }
    /**
     * Navigates to the previous slide in the list.
     * If the current slide is the first one, it wraps around to the last slide.
     *
     * @param onSlideChanged A callback function that is invoked with the index of the new slide.
     */
    fun previousSlide(onSlideChanged: (slideIndex: Int) -> Unit) {
        if (selectedSlideIndex > 0) selectedSlideIndex--
        else selectedSlideIndex = slides.size - 1
        selectedSlide.value = slides[selectedSlideIndex]
        onSlideChanged(selectedSlideIndex)
        saveSlide()
    }


    /**
     * Toggles the visibility state of the slide controls.
     * It cycles through ShowControls -> ShowReciter -> ShowVerse -> HideAll -> ShowControls.
     */
    fun toggleSlideControls() {
        slideControls.value = when (slideControls.value) {
            is SlideControls.ShowControls -> SlideControls.ShowReciter
            is SlideControls.ShowReciter -> SlideControls.ShowVerse
            is SlideControls.ShowVerse -> SlideControls.HideAll
            else -> SlideControls.ShowControls
        }
    }

    /**
     * Shows the verse information if the controls are currently hidden.
     */
    fun showVerse() {
        if (slideControls.value is SlideControls.HideAll) {
            slideControls.value = SlideControls.ShowVerse
        }
    }

    /**
     * Sets the slide controls to the 'ShowControls' state.
     */
    fun showControls() {
        slideControls.value = SlideControls.ShowControls
    }

}

