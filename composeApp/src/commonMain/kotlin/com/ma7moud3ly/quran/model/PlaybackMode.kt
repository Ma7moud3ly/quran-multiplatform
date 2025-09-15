package com.ma7moud3ly.quran.model


/**
 * Represents the different modes for playing back recitations.
 */
sealed interface PlaybackMode {
    /**
     * Playback mode where a single reciter is used for the entire recitation.
     */
    data object Single : PlaybackMode

    /**
     * Playback mode where the same verses are recited by several reciters.
     */
    data object Repetitive : PlaybackMode

    /**
     * Playback mode where the reciter changes for each verse.
     */
    data object Shuffling : PlaybackMode

    /**
     * Playback mode where the total set of verses are divided among the selected reciters.
     */
    data object Distributed : PlaybackMode
}

val PlaybackMode.isSingleReciter get() = this is PlaybackMode.Single
val PlaybackMode.isShuffling get() = this is PlaybackMode.Shuffling
val PlaybackMode.isDistributed get() = this is PlaybackMode.Distributed
val PlaybackMode.toInt
    get() = when (this) {
        is PlaybackMode.Single -> 0
        is PlaybackMode.Repetitive -> 1
        is PlaybackMode.Shuffling -> 2
        is PlaybackMode.Distributed -> 3
    }
val Int.toPlaybackMode
    get() = when (this) {
        0 -> PlaybackMode.Single
        1 -> PlaybackMode.Repetitive
        2 -> PlaybackMode.Shuffling
        3 -> PlaybackMode.Distributed
        else -> PlaybackMode.Single
    }
