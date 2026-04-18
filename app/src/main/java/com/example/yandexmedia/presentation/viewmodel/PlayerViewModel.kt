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

    private val _state = MutableLiveData<PlayerState>(PlayerState.Idle)
    val state: LiveData<PlayerState> = _state

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private var currentTrack: Track? = null
    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false
    private var completed = false

    private val updater = object : Runnable {
        override fun run() {
            val pos = mediaPlayer?.currentPosition ?: 0
            _state.value = PlayerState.Playing(format(pos))
            handler.postDelayed(this, 300)
        }
    }

    fun setTrack(track: Track) {
        currentTrack = track

        viewModelScope.launch {
            _isFavorite.postValue(favoritesInteractor.isFavorite(track.trackId))
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        val favoriteNow = _isFavorite.value ?: false

        viewModelScope.launch {
            if (favoriteNow) {
                favoritesInteractor.removeTrack(track)
                _isFavorite.postValue(false)
            } else {
                favoritesInteractor.addTrack(track)
                _isFavorite.postValue(true)
            }
        }
    }

    fun prepare(url: String) {
        if (url.isBlank()) {
            _state.value = PlayerState.Error(null)
            return
        }

        releasePlayer()

        prepared = false
        completed = false
        _state.value = PlayerState.Idle

        mediaPlayer = mediaPlayerProvider.create().apply {
            try {
                setDataSource(url)
                setOnPreparedListener {
                    prepared = true
                    _state.value = PlayerState.Prepared
                }
                setOnCompletionListener {
                    completed = true
                    stopUpdates()
                    _state.value = PlayerState.Completed
                }
                prepareAsync()
            } catch (t: Throwable) {
                prepared = false
                completed = false
                _state.value = PlayerState.Error(t)
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
            _state.value = PlayerState.Paused(format(player.currentPosition))
        } else {
            if (completed) {
                player.seekTo(0)
                completed = false
            }
            player.start()
            startUpdates()
            _state.value = PlayerState.Playing(format(player.currentPosition))
        }
    }

    fun release() {
        prepared = false
        completed = false
        _state.value = PlayerState.Idle
        releasePlayer()
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
        val s = ms / 1000
        return String.format("%02d:%02d", s / 60, s % 60)
    }
}