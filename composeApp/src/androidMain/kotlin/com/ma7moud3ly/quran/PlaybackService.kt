package com.ma7moud3ly.quran

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.snapshotFlow
import androidx.core.app.NotificationCompat
import com.ma7moud3ly.quran.managers.MediaPlayerManager
import com.ma7moud3ly.quran.platform.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PlaybackService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mediaPlayerManager: MediaPlayerManager by inject()

    companion object {
        private const val TAG = "PlaybackService"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "PlaybackServiceChannel"
        private const val PACKAGE = "com.ma7moud3ly.quran"
        const val ACTION_STOP_PLAYBACK = "$PACKAGE.ACTION_STOP_PLAYBACK"
        const val ACTION_PAUSE_PLAYBACK = "$PACKAGE.ACTION_PAUSE_PLAYBACK"
        const val ACTION_RESUME_PLAYBACK = "$PACKAGE.ACTION_RESUME_PLAYBACK"
        const val ACTION_HIDE_PLAYBACK_NOTIFICATION = "$PACKAGE.HIDE_PLAYBACK_NOTIFICATION"
        const val OPEN_PLAY_BACK = "open_playback_screen"
    }

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate")
        initNotifications()
    }

    @OptIn(FlowPreview::class)
    private fun initNotifications() {
        Log.i(TAG, "initNotifications")
        createNotificationChannel()
        serviceScope.launch {
            delay(500)
            startForeground(NOTIFICATION_ID, createNotification())
        }
        serviceScope.launch {
            snapshotFlow { mediaPlayerManager.isPlaying.value }
                .debounce { 500 }
                .distinctUntilChanged()
                .collect {

                    updateNotification()
                }
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_STOP_PLAYBACK -> {
                stopPlaybackServiceAndRelease()
                return START_NOT_STICKY
            }

            ACTION_HIDE_PLAYBACK_NOTIFICATION -> {
                stopPlaybackService()
                return START_NOT_STICKY
            }

            ACTION_PAUSE_PLAYBACK -> {
                mediaPlayerManager.pause()
                updateNotification()
                return START_STICKY
            }

            ACTION_RESUME_PLAYBACK -> {
                mediaPlayerManager.resume()
                updateNotification()
                return START_STICKY
            }

            else -> START_STICKY
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Playback Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        Log.v(TAG, "createNotification")
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(OPEN_PLAY_BACK, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Intent for the "Stop" action
        val stopPlaybackIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_STOP_PLAYBACK
        }
        val stopPlaybackPendingIntent = PendingIntent.getService(
            this, 0, stopPlaybackIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Intent for the "pause" action
        val pausePlaybackIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_PAUSE_PLAYBACK
        }
        val pausePlaybackPendingIntent = PendingIntent.getService(
            this, 0, pausePlaybackIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Intent for the "resume" action
        val resumePlaybackIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_RESUME_PLAYBACK
        }
        val resumePlaybackPendingIntent = PendingIntent.getService(
            this, 0, resumePlaybackIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val deleteIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_STOP_PLAYBACK
        }
        val deletePendingIntent = PendingIntent.getService(
            this,
            3,
            deleteIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val reciter = getString(
            R.string.playback_reciter,
            mediaPlayerManager.reciterName.value
        )
        val chapter = getString(
            R.string.playback_chapter,
            mediaPlayerManager.getChapter().name
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(chapter)
            .setContentText(reciter)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSmallIcon(R.drawable.notifications_icon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setDeleteIntent(deletePendingIntent)
            .setOnlyAlertOnce(true)
            .addAction(
                R.drawable.playback_stop,
                getString(R.string.playback_stop),
                stopPlaybackPendingIntent
            )
        if (mediaPlayerManager.isPlaying()) builder.addAction(
            R.drawable.playback_pause,
            getString(R.string.playback_pause),
            pausePlaybackPendingIntent
        ) else builder.addAction(
            R.drawable.playback_play,
            getString(R.string.playback_play),
            resumePlaybackPendingIntent
        )
        return builder.build()
    }

    private fun stopPlaybackServiceAndRelease() {
        Log.v(TAG, "stopServiceAndRelease")
        mediaPlayerManager.releaseFromBackground()
        stopPlaybackService()
    }

    private fun stopPlaybackService() {
        Log.v(TAG, "stopPlaybackService")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "onDestroy")
        serviceScope.coroutineContext.cancel()
    }
}