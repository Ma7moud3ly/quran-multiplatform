package com.ma7moud3ly.quran.model

import androidx.compose.runtime.mutableStateOf


data class RecitationState(
    private val canChangeChapter: Boolean = true,
    private val canChangeReciter: Boolean = true,
    private val canChangeVerse: Boolean = true,
    private val firstVerse: Int = 1,
    private val lastVerse: Int = 1,
    private val singleVerse: Boolean = canChangeVerse.not(),
    private val reelMode: Boolean = false,
    private val playInBackground: Boolean = false,
    private val playbackMode: PlaybackMode = PlaybackMode.Single
) {

    val canChangeChapterState = mutableStateOf(canChangeChapter)
    val canChangeReciterState = mutableStateOf(canChangeReciter)
    val canChangeVerseState = mutableStateOf(canChangeVerse)

    val firstVerseState = mutableStateOf(firstVerse)
    val lastVerseState = mutableStateOf(lastVerse)
    val singleVerseState = mutableStateOf(singleVerse)
    val reelModeState = mutableStateOf(reelMode)
    val playbackModeState = mutableStateOf(playbackMode)
    val playInBgState = mutableStateOf(playInBackground)

    fun getReelMode() = reelModeState.value
    fun getPlayInBackground() = playInBgState.value
    fun getFirstVerse() = firstVerseState.value
    fun getLastVerse(): Int {
        return if (singleVerseState.value) firstVerseState.value
        else lastVerseState.value
    }

    fun getPlaybackMode() = playbackModeState.value

    fun isSingleReciterMode() = playbackModeState.value.isSingleReciter
    fun isMultipleReciterMode() = playbackModeState.value.isSingleReciter.not()

    fun setPlaybackMode(value: PlaybackMode) {
        playbackModeState.value = value
    }

    fun setFirstVerse(value: Int) {
        firstVerseState.value = value
    }

    fun setLastVerse(value: Int) {
        lastVerseState.value = value
    }

    fun setSingleVerse(value: Boolean) {
        singleVerseState.value = value
    }

    fun setContentLock(
        canChangeChapter: Boolean,
        canChangeReciter: Boolean,
        canChangeVerse: Boolean
    ) {
        canChangeChapterState.value = canChangeChapter
        canChangeReciterState.value = canChangeReciter
        canChangeVerseState.value = canChangeVerse
    }


}