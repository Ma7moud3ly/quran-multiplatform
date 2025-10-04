package com.ma7moud3ly.quran.managers

import androidx.compose.runtime.mutableStateOf
import com.ma7moud3ly.quran.data.repository.BackgroundsRepository
import com.ma7moud3ly.quran.model.TvBackground
import com.ma7moud3ly.quran.model.TvControls
import org.koin.core.annotation.Factory

/**
 * Manages the display and navigation of TV backgrounds.
 *
 * This class is responsible for loading backgrounds, handling background selection,
 * and managing the visibility of background controls.
 *
 * @property backgroundsRepository The repository for accessing background data.
 */
@Factory
class BackgroundsManager(
    private val backgroundsRepository: BackgroundsRepository
) {
    /** A list of all available TV backgrounds. */
    val backgrounds: List<TvBackground> get() = backgroundsRepository.getBackgrounds()

    /** The currently selected TV background. */
    val selectedBackground = mutableStateOf(backgrounds.first())

    /** The current state of the background controls (e.g., show controls, show reciter). */
    val tvControls = mutableStateOf<TvControls>(TvControls.ShowControls)

    /** The index of the currently selected background in the [backgrounds] list. */
    private var selectedBackgroundIndex = 0

    init {
        val background = backgroundsRepository.getSelectedBackground()
        selectedBackground.value = background
        selectedBackgroundIndex = backgrounds.indexOf(background)
    }


    /**
     * Saves the ID of the currently selected background to the settings.
     */
    private fun saveBackground() {
        backgroundsRepository.selectBackground(selectedBackground.value)
    }

    /**
     * Selects a specific background to be displayed.
     *
     * @param background The [TvBackground] to select.
     */
    fun selectBackground(background: TvBackground) {
        selectedBackground.value = background
        saveBackground()
    }

    /**
     * Navigates to the next background in the list.
     * If the current background is the last one, it wraps around to the first background.
     *
     * @param onBackgroundChanged A callback function that is invoked with the index of the new background.
     */
    fun nextBackground(onBackgroundChanged: (backgroundIndex: Int) -> Unit) {
        if (selectedBackgroundIndex < backgrounds.size - 1) selectedBackgroundIndex++
        else selectedBackgroundIndex = 0
        selectedBackground.value = backgrounds[selectedBackgroundIndex]
        onBackgroundChanged(selectedBackgroundIndex)
        saveBackground()
    }

    /**
     * Navigates to the previous background in the list.
     * If the current background is the first one, it wraps around to the last background.
     *
     * @param onBackgroundChanged A callback function that is invoked with the index of the new background.
     */
    fun previousBackground(onBackgroundChanged: (backgroundIndex: Int) -> Unit) {
        if (selectedBackgroundIndex > 0) selectedBackgroundIndex--
        else selectedBackgroundIndex = backgrounds.size - 1
        selectedBackground.value = backgrounds[selectedBackgroundIndex]
        onBackgroundChanged(selectedBackgroundIndex)
        saveBackground()
    }


    /**
     * Toggles the visibility state of the background controls.
     * It cycles through ShowControls -> ShowReciter -> ShowVerse -> HideAll -> ShowControls.
     */
    fun toggleBackgroundControls() {
        tvControls.value = when (tvControls.value) {
            is TvControls.ShowControls -> TvControls.ShowTitle
            is TvControls.ShowTitle -> TvControls.ShowVerse
            is TvControls.ShowVerse -> TvControls.HideAll
            else -> TvControls.ShowControls
        }
    }

    /**
     * Shows the verse information if the controls are currently hidden.
     */
    fun showVerse() {
        if (tvControls.value is TvControls.HideAll) {
            tvControls.value = TvControls.ShowVerse
        }
    }

    /**
     * Sets the background controls to the 'ShowControls' state.
     */
    fun showControls() {
        tvControls.value = TvControls.ShowControls
    }

}

