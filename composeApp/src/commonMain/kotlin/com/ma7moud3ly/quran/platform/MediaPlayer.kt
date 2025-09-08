package com.ma7moud3ly.quran.platform

import com.ma7moud3ly.quran.model.MediaFile

/**
 * MediaPlayer class that provides a common interface for playing media files on different platforms.
 *
 * This class is an expect class, which means that it defines a common API that can be implemented
 * differently on different platforms (e.g., Android, iOS, desktop).
 */
expect class MediaPlayer() {
    /**
     * Prepares the media player for playback.
     * @param mediaFile The media file to be played.
     * @param onPrepared A callback function that will be invoked when the media player is prepared.
     */
    fun prepare(mediaFile: MediaFile, onPrepared: () -> Unit)

    /**
     * Starts playback of the media file.
     * @param onCompletion A callback function that will be invoked when playback is complete.
     */
    fun start(onCompletion: () -> Unit)

    fun start()

    /**
     * Pauses playback of the media file.
     */
    fun pause()

    /**
     * Releases the media player and its resources.
     */
    fun release()

    /**
     * Checks if the media player is currently playing.
     * @return True if the media player is playing, false otherwise.
     */
    fun isPlaying(): Boolean

    /**
     * Plays the media file in the background.
     * This method is platform-specific and may not be available on all platforms.
     */
    fun playInBackground()

    /**
     * Hides the background notification for playback.
     * This method is platform-specific and may not be available on all platforms.
     */
    fun hideBackgroundNotification()

    /**
     * Releases the background service associated with playback.
     * This method is platform-specific and may not be available on all platforms.
     */
    fun releaseBackgroundService()
}

