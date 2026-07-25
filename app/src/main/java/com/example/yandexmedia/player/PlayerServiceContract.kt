package com.example.yandexmedia.player

import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceContract {
    val playbackState: StateFlow<PlaybackState>

    fun prepare(track: Track)
    fun playPause()
    fun releasePlayer()
    fun showForegroundNotification()
    fun hideForegroundNotification()
}

data class PlaybackState(
    val status: Status = Status.IDLE,
    val currentPositionMillis: Int = 0,
    val isPlayButtonEnabled: Boolean = false,
    val error: Throwable? = null
) {
    enum class Status { IDLE, PREPARED, PLAYING, PAUSED, COMPLETED, ERROR }
}
