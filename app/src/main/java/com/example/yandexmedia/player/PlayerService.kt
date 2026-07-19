package com.example.yandexmedia.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerService : Service(), PlayerServiceContract {

    private val binder = PlayerBinder()
    private val handler = Handler(Looper.getMainLooper())
    private val mutablePlaybackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = mutablePlaybackState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: Track? = null
    private var prepared = false
    private var completed = false

    private val positionUpdater = object : Runnable {
        override fun run() {
            val player = mediaPlayer ?: return
            mutablePlaybackState.value = mutablePlaybackState.value.copy(
                status = PlaybackState.Status.PLAYING,
                currentPositionMillis = player.currentPosition,
                isPlayButtonEnabled = true
            )
            handler.postDelayed(this, POSITION_UPDATE_DELAY)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        intent.getParcelableExtra<Track>(EXTRA_TRACK)?.let(::prepare)
        return binder
    }

    override fun prepare(track: Track) {
        if (currentTrack?.previewUrl == track.previewUrl && mediaPlayer != null) return
        currentTrack = track
        releaseMediaPlayer(resetState = false)

        if (track.previewUrl.isBlank()) {
            mutablePlaybackState.value = PlaybackState(
                status = PlaybackState.Status.ERROR,
                error = IllegalArgumentException("Track preview URL is empty")
            )
            return
        }

        mutablePlaybackState.value = PlaybackState()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(track.previewUrl)
                setOnPreparedListener {
                    prepared = true
                    mutablePlaybackState.value = PlaybackState(
                        status = PlaybackState.Status.PREPARED,
                        isPlayButtonEnabled = true
                    )
                }
                setOnCompletionListener {
                    completed = true
                    stopPositionUpdates()
                    hideForegroundNotification()
                    mutablePlaybackState.value = PlaybackState(
                        status = PlaybackState.Status.COMPLETED,
                        isPlayButtonEnabled = true
                    )
                }
                setOnErrorListener { _, what, extra ->
                    stopPositionUpdates()
                    hideForegroundNotification()
                    mutablePlaybackState.value = PlaybackState(
                        status = PlaybackState.Status.ERROR,
                        error = IllegalStateException("MediaPlayer error: $what/$extra")
                    )
                    true
                }
                prepareAsync()
            } catch (error: Throwable) {
                mutablePlaybackState.value = PlaybackState(
                    status = PlaybackState.Status.ERROR,
                    error = error
                )
                releaseMediaPlayer(resetState = false)
            }
        }
    }

    override fun playPause() {
        val player = mediaPlayer ?: return
        if (!prepared) return

        if (player.isPlaying) {
            player.pause()
            stopPositionUpdates()
            hideForegroundNotification()
            mutablePlaybackState.value = mutablePlaybackState.value.copy(
                status = PlaybackState.Status.PAUSED,
                currentPositionMillis = player.currentPosition
            )
        } else {
            if (completed) {
                player.seekTo(0)
                completed = false
            }
            player.start()
            mutablePlaybackState.value = mutablePlaybackState.value.copy(
                status = PlaybackState.Status.PLAYING,
                isPlayButtonEnabled = true
            )
            handler.post(positionUpdater)
        }
    }

    override fun releasePlayer() {
        hideForegroundNotification()
        releaseMediaPlayer(resetState = true)
    }

    override fun showForegroundNotification() {
        if (mediaPlayer?.isPlaying != true) return
        val track = currentTrack ?: return
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_media)
            .setContentTitle(getString(R.string.player_notification_title))
            .setContentText("${track.artistName} - ${track.trackName}")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun hideForegroundNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        releasePlayer()
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun releaseMediaPlayer(resetState: Boolean) {
        stopPositionUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
        prepared = false
        completed = false
        if (resetState) mutablePlaybackState.value = PlaybackState()
    }

    private fun stopPositionUpdates() {
        handler.removeCallbacks(positionUpdater)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.player_notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerServiceContract = this@PlayerService
    }

    companion object {
        const val EXTRA_TRACK = "player_track"
        private const val NOTIFICATION_CHANNEL_ID = "player_playback"
        private const val NOTIFICATION_ID = 101
        private const val POSITION_UPDATE_DELAY = 300L
    }
}
