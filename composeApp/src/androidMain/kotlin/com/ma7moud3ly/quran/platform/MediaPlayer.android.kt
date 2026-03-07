package com.ma7moud3ly.quran.platform

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.core.content.ContextCompat
import com.ma7moud3ly.quran.model.MediaFile

object PlaybackConstants {
    private const val PACKAGE = "com.ma7moud3ly.quran"
    const val PLAYBACK_SERVICE_CLASS = "com.ma7moud3ly.quran.app.PlaybackService"
    const val ACTION_STOP_PLAYBACK = "$PACKAGE.ACTION_STOP_PLAYBACK"
    const val ACTION_PAUSE_PLAYBACK = "$PACKAGE.ACTION_PAUSE_PLAYBACK"
    const val ACTION_RESUME_PLAYBACK = "$PACKAGE.ACTION_RESUME_PLAYBACK"
    const val ACTION_HIDE_PLAYBACK_NOTIFICATION = "$PACKAGE.HIDE_PLAYBACK_NOTIFICATION"
    const val ACTION_OPEN_PLAYBACK = "$PACKAGE.OPEN_PLAYBACK"
}


actual class MediaPlayer : MediaPlayer.OnCompletionListener {
    private var onComplete: (() -> Unit)? = null
    private var mediaPlayer: MediaPlayer? = null

    actual fun prepare(
        mediaFile: MediaFile,
        onPrepared: () -> Unit
    ) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(mediaFile.path)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener {
                Log.v(TAG, "onPrepared")
                onPrepared()
            }
            setOnCompletionListener(this@MediaPlayer)
            prepareAsync()
        }
    }

    actual fun start() {
        Log.v(TAG, "start requested")
        mediaPlayer?.start()
    }

    actual fun start(onCompletion: () -> Unit) {
        this.onComplete = onCompletion
        start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.v(TAG, "onCompletion")
        onComplete?.invoke()
    }


    actual fun pause() {
        Log.v(TAG, "pause")
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    actual fun release() {
        Log.v(TAG, "release")
        mediaPlayer?.release()
        mediaPlayer = null
    }

    actual fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }


    actual fun playInBackground() {
        val context = AndroidApp.getContext() ?: return
        val isNotificationsPermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED)
            } else true

        if (isNotificationsPermissionGranted) {
            // Audio focus should be requested by start() before service is started
            // Or ensure service requests focus if it controls playback initiation
            val serviceIntent = Intent().apply {
                setClassName(context.packageName, PlaybackConstants.PLAYBACK_SERVICE_CLASS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }

    actual fun hideBackgroundNotification() {
        val context = AndroidApp.getContext() ?: return
        val stopIntent = Intent().apply {
            setClassName(context.packageName, PlaybackConstants.PLAYBACK_SERVICE_CLASS)
            action = PlaybackConstants.ACTION_HIDE_PLAYBACK_NOTIFICATION
        }
        context.stopService(stopIntent)
    }

    actual fun releaseBackgroundService() {
        val context = AndroidApp.getContext() ?: return
        val stopIntent = Intent().apply {
            setClassName(context.packageName, PlaybackConstants.PLAYBACK_SERVICE_CLASS)
            action = PlaybackConstants.ACTION_STOP_PLAYBACK

        }
        context.stopService(stopIntent)
    }

    companion object {
        private const val TAG = "MediaPlayer"
    }
}
