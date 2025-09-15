package com.ma7moud3ly.quran.managers

import androidx.compose.foundation.lazy.LazyListState
import com.ma7moud3ly.quran.model.Verse
import com.ma7moud3ly.quran.platform.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

/**
 * Manages a list of Quran verses, allowing selection, navigation, and interaction with a LazyListState.
 *
 * @property initialVerseId The ID of the verse to be initially selected. Defaults to the first verse if null.
 * @property verses The list of [Verse] objects to manage. This list cannot be empty.
 */
class VersesManager(
    private val initialVerseId: Int? = null,
    val verses: List<Verse>,
) {
    init {
        require(verses.isNotEmpty()) { "verses list cannot be empty." }
    }

    private val firstVerseIndex: Int = verses.first().id - 1
    private val lastVerseIndex: Int = verses.last().id - 1
    private val versesMap: Map<Int, Verse> = verses.associateBy { it.id - 1 }
    val initialVerse: Verse get() = versesMap[(initialVerseId ?: 1) - 1] ?: verses.first()
    private val _selectedVerse = MutableStateFlow<Verse?>(initialVerse)

    /**
     * A [Flow] emitting the currently selected [Verse].
     */
    val selectedVerse: Flow<Verse?> = _selectedVerse.asStateFlow()

    /**
     * The ID (number) of the currently selected verse.
     */
    val selectedVerseId: Int get() = _selectedVerse.value?.id ?: 1

    /**
     * The index of the currently selected verse within the [verses] list.
     * This is calculated as `selectedVerseId - 1`.
     */
    val selectedVerseIndex: Int get() = selectedVerseId - 1

    /**
     * The index of the currently selected verse relative to the `initialVerse`.
     * This is calculated as the difference between the ID of the `selectedVerse` and the ID of the `initialVerse`.
     * It represents the position of the selected verse within the range starting from the `initialVerse`.
     */
    val selectedRangeIndex get() = selectedVerseId - initialVerse.id

    /**
     * Handles scrolling the [LazyListState] to keep the [selectedVerse] visible.
     * If the selected verse is not visible, it scrolls to it.
     * Uses `animateScrollToItem` for short distances and `scrollToItem` for longer distances.
     * @param listState The [LazyListState] to manage.
     */
    suspend fun handleVersesScrollbar(listState: LazyListState) {
        selectedVerse.collect { verse ->
            val index = selectedVerseIndex
            val totalItems = listState.layoutInfo.totalItemsCount
            if (index >= totalItems) return@collect
            val layoutInfo = listState.layoutInfo.visibleItemsInfo
            val firstVisibleItemIndex = listState.firstVisibleItemIndex
            val hiddenForward = layoutInfo.none { it.index > index }
            val hiddenBackward = firstVisibleItemIndex > index
            if (hiddenForward || hiddenBackward) {
                Log.v(TAG, "VersesScrollBar moves to verse - ${index + 1}")
                val distance = abs(index - firstVisibleItemIndex)
                if (distance > 15) listState.scrollToItem(index)
                else listState.animateScrollToItem(index)
            }
        }
    }

    /**
     * Checks if there is a next verse available after the currently selected verse.
     * @return `true` if there is a next verse, `false` otherwise.
     */
    fun hasNext(): Boolean = selectedVerseIndex < lastVerseIndex

    /**
     * Checks if there is a previous verse available before the currently selected verse,
     * considering the `initialVerse` as the lower bound.
     * @return `true` if there is a previous verse within the allowed range, `false` otherwise.
     */
    fun hasPrevious(): Boolean = selectedVerseIndex > initialVerse.id - 1


    /**
     * Retrieves the next verse in the sequence after the currently selected verse.
     *
     * @return The next [Verse] if it exists, otherwise `null` if the current verse is the last one.
     */
    fun getNextVerse(): Verse? {
        return if (hasNext()) versesMap[selectedVerseIndex + 1]
        else null
    }

    /**
     * Selects the previous verse if available.
     * @return `true` if a previous verse was selected, `false` if the current verse is the first one.
     */
    fun previousVerse(): Boolean {
        val currentIndex = selectedVerseIndex
        return if (currentIndex > firstVerseIndex) {
            selectVerse(versesMap[currentIndex - 1])
            true
        } else false
    }


    /**
     * Selects the next verse if available.
     * @return `true` if a next verse was selected, `false` if the current verse is the last one.
     */
    fun nextForwardVerse(): Boolean {
        val currentIndex = selectedVerseIndex
        return if (currentIndex < lastVerseIndex) {
            selectVerse(versesMap[currentIndex + 1])
            true
        } else false
    }

    /**
     * Selects the previous verse if it's within the range defined by the `initialVerse`.
     * The `initialVerse` marks the beginning of the selectable range when moving backward.
     * @return `true` if a previous verse within the allowed range was selected, `false` otherwise.
     */
    fun previousVerseInRange(): Boolean {
        val currentIndex = selectedVerseIndex
        val firstVerseIndexInSelectedRange = initialVerse.id - 1
        return if (currentIndex > firstVerseIndexInSelectedRange) {
            selectVerse(versesMap[currentIndex - 1])
            true
        } else false
    }

    /**
     * Selects the given [Verse].
     * If the provided verse is `null`, no action is taken.
     * @param verse The [Verse] to select.
     */
    fun selectVerse(verse: Verse?) {
        if (verse != null) {
            Log.v(TAG, " selectVerse ${verse.id}")
            _selectedVerse.value = verse
        }
    }

    /**
     * Resets the selected verse to the [initialVerse].
     * This involves temporarily emitting `null` for [selectedVerse] before re-emitting the [initialVerse].
     * This is invoked when the reciter is changed during playback.
     * A delay is introduced to allow UI updates if needed.
     */
    suspend fun reset() {
        Log.v(TAG, " reset to initialVerse ${initialVerse.id}")
        _selectedVerse.emit(null)
        delay(1000)
        _selectedVerse.emit(initialVerse)
    }

    companion object {
        /**
         * Tag for logging purposes.
         */
        private const val TAG = "VersesManager"
    }
}
