package com.example.yandexmedia.presentation.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.di.MediaPlayerProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val mediaPlayerProvider: MediaPlayerProvider
) : ViewModel() {

    private val _state = MutableLiveData<PlayerState>(PlayerState.Idle)
    val state: LiveData<PlayerState> = _state

    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false
    private var completed = false
    private var progressJob: Job? = null

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
                    _state.postValue(PlayerState.Prepared)
                }
                setOnCompletionListener {
                    completed = true
                    stopProgressUpdates()
                    _state.postValue(PlayerState.Completed)
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
            stopProgressUpdates()
            _state.value = PlayerState.Paused(format(player.currentPosition))
        } else {
            if (completed) {
                player.seekTo(0)
                completed = false
            }
            player.start()
            _state.value = PlayerState.Playing(format(player.currentPosition))
            startProgressUpdates()
        }
    }

    fun release() {
        prepared = false
        completed = false
        _state.value = PlayerState.Idle
        releasePlayer()
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                val position = mediaPlayer?.currentPosition ?: 0
                _state.postValue(PlayerState.Playing(format(position)))
                delay(300)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun releasePlayer() {
        stopProgressUpdates()
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
        val totalSeconds = ms / 1000
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)
    }
}