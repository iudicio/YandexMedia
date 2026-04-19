package com.example.yandexmedia.presentation.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.di.MediaPlayerProvider
import com.example.yandexmedia.domain.interactor.FavoritesInteractor
import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val handler: Handler,
    private val mediaPlayerProvider: MediaPlayerProvider,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    val state: LiveData<PlayerState> = _state

    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false
    private var completed = false
    private var currentTrack: Track? = null

    private val updater = object : Runnable {
        override fun run() {
            val pos = mediaPlayer?.currentPosition ?: 0
            updateState(
                playbackState = PlayerState.PlaybackState.Playing,
                currentPosition = format(pos),
                isPlayButtonEnabled = true
            )
            handler.postDelayed(this, 300)
        }
    }

    fun prepare(track: Track) {
        currentTrack = track

        if (track.previewUrl.isBlank()) {
            _state.value = PlayerState(
                playbackState = PlayerState.PlaybackState.Error,
                currentPosition = "00:00",
                isPlayButtonEnabled = false,
                isFavourite = false,
                error = null
            )
            return
        }

        releasePlayer()

        prepared = false
        completed = false

        _state.value = PlayerState(
            playbackState = PlayerState.PlaybackState.Idle,
            currentPosition = "00:00",
            isPlayButtonEnabled = false,
            isFavourite = false,
            error = null
        )

        viewModelScope.launch {
            val isFavourite = favoritesInteractor.isFavorite(track.trackId)
            updateFavourite(isFavourite)
        }

        mediaPlayer = mediaPlayerProvider.create().apply {
            try {
                setDataSource(track.previewUrl)

                setOnPreparedListener {
                    prepared = true
                    updateState(
                        playbackState = PlayerState.PlaybackState.Prepared,
                        currentPosition = "00:00",
                        isPlayButtonEnabled = true
                    )
                }

                setOnCompletionListener {
                    completed = true
                    stopUpdates()
                    updateState(
                        playbackState = PlayerState.PlaybackState.Completed,
                        currentPosition = "00:00",
                        isPlayButtonEnabled = true
                    )
                }

                prepareAsync()
            } catch (t: Throwable) {
                prepared = false
                completed = false

                val current = _state.value ?: PlayerState()
                _state.value = current.copy(
                    playbackState = PlayerState.PlaybackState.Error,
                    currentPosition = "00:00",
                    isPlayButtonEnabled = false,
                    error = t
                )

                releasePlayer()
            }
        }
    }

    fun onPlayPause() {
        val player = mediaPlayer ?: return
        if (!prepared) return

        if (player.isPlaying) {
            player.pause()
            stopUpdates()
            updateState(
                playbackState = PlayerState.PlaybackState.Paused,
                currentPosition = format(player.currentPosition),
                isPlayButtonEnabled = true
            )
        } else {
            if (completed) {
                player.seekTo(0)
                completed = false
            }

            player.start()
            startUpdates()
            updateState(
                playbackState = PlayerState.PlaybackState.Playing,
                currentPosition = format(player.currentPosition),
                isPlayButtonEnabled = true
            )
        }
    }

    fun onFavouriteClicked() {
        val track = currentTrack ?: return
        val currentState = _state.value ?: PlayerState()

        viewModelScope.launch {
            if (currentState.isFavourite) {
                favoritesInteractor.removeTrack(track)
                updateFavourite(false)
            } else {
                favoritesInteractor.addTrack(track)
                updateFavourite(true)
            }
        }
    }

    fun release() {
        prepared = false
        completed = false

        val current = _state.value ?: PlayerState()
        _state.value = current.copy(
            playbackState = PlayerState.PlaybackState.Idle,
            currentPosition = "00:00",
            isPlayButtonEnabled = false,
            error = null
        )

        releasePlayer()
    }

    private fun updateState(
        playbackState: PlayerState.PlaybackState,
        currentPosition: String,
        isPlayButtonEnabled: Boolean
    ) {
        val current = _state.value ?: PlayerState()
        _state.value = current.copy(
            playbackState = playbackState,
            currentPosition = currentPosition,
            isPlayButtonEnabled = isPlayButtonEnabled,
            error = null
        )
    }

    private fun updateFavourite(isFavourite: Boolean) {
        val current = _state.value ?: PlayerState()
        _state.postValue(
            current.copy(isFavourite = isFavourite)
        )
    }

    private fun startUpdates() {
        handler.removeCallbacks(updater)
        handler.post(updater)
    }

    private fun stopUpdates() {
        handler.removeCallbacks(updater)
    }

    private fun releasePlayer() {
        stopUpdates()
        try {
            mediaPlayer?.reset()
            mediaPlayer?.release()
        } catch (_: Throwable) {
        }
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun format(ms: Int): String {
        val seconds = ms / 1000
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }
}