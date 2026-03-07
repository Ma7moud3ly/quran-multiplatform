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
    val selectedBackground = backgroundsRepository.selectedBackground

    /** The current state of the background controls (e.g., show controls, show reciter). */
    val tvControls = mutableStateOf<TvControls>(TvControls.ShowControls)

    /** The index of the currently selected background in the [backgrounds] list. */
    private var selectedBackgroundIndex = 0

    init {
        val background = backgroundsRepository.getSelectedBackground()
        selectedBackgroundIndex = backgrounds.indexOf(background)
    }


    /**
     * Selects a specific background to be displayed.
     *
     * @param background The [TvBackground] to select.
     */
    fun selectBackground(background: TvBackground) {
        backgroundsRepository.selectBackground(background)
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
        onBackgroundChanged(selectedBackgroundIndex)
        val background = backgrounds[selectedBackgroundIndex]
        backgroundsRepository.selectBackground(background)
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
        onBackgroundChanged(selectedBackgroundIndex)
        val background = backgrounds[selectedBackgroundIndex]
        backgroundsRepository.selectBackground(background)
    }


    /**
     * Toggles the visibility state of the background controls.
     * It cycles through ShowControls -> ShowReciter -> ShowVerse -> HideAll -> ShowControls.
     */
    fun toggleBackgroundControls() {
        tvControls.value = when (tvControls.value) {
            is TvControls.ShowControls -> TvControls.ShowVerseAndHeader
            is TvControls.ShowVerseAndHeader -> TvControls.ShowVerse
            is TvControls.ShowVerse -> TvControls.ShowVerseAndChapter
            is TvControls.ShowVerseAndChapter -> TvControls.ShowReciter
            is TvControls.ShowReciter -> TvControls.HideAll
            else -> TvControls.ShowControls
        }
    }

    /**
     * Shows the verse content.
     */
    fun showVerseContent() {
        tvControls.value = TvControls.ShowVerse
    }

    /**
     * Shows Recitation details.
     */
    fun showReciter() {
        tvControls.value = TvControls.ShowReciter
    }


    /**
     * Sets the background controls to the 'ShowControls' state.
     */
    fun showControls() {
        tvControls.value = TvControls.ShowControls
    }

    val hideAllControls: Boolean get() = tvControls.value is TvControls.HideAll

}

