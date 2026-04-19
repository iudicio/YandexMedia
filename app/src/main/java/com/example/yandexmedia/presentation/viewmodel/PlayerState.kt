package com.example.yandexmedia.presentation.viewmodel

data class PlayerState(
    val playbackState: PlaybackState = PlaybackState.Idle,
    val currentPosition: String = "00:00",
    val isPlayButtonEnabled: Boolean = false,
    val isFavourite: Boolean = false,
    val error: Throwable? = null
) {
    enum class PlaybackState {
        Idle,
        Prepared,
        Playing,
        Paused,
        Completed,
        Error
    }
}