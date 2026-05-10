package com.example.yandexmedia.presentation.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.di.MediaPlayerProvider
import com.example.yandexmedia.domain.interactor.FavoritesInteractor
import com.example.yandexmedia.domain.interactor.PlaylistsInteractor
import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val handler: Handler,
    private val mediaPlayerProvider: MediaPlayerProvider,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    val state: LiveData<PlayerState> = _state

    val playlists = playlistsInteractor.getPlaylists().asLiveData()

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
    fun addTrackToPlaylist(
        playlistId: Long,
        trackId: Long,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val isAdded = playlistsInteractor.addTrackToPlaylist(
                playlistId = playlistId,
                trackId = trackId
            )
            onResult(isAdded)
        }
    }
    fun prepare(track: Track) {
        currentTrack = track

        if (track.previewUrl.isBlank()) {
            _state.value = PlayerState(
                playbackState = PlayerState.PlaybackState.Error,
                currentPosition = "00:00",
                isPlayButtonEnabled = false,
                isFavourite = false
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
            isFavourite = false
        )

        viewModelScope.launch {
            updateFavourite(favoritesInteractor.isFavorite(track.trackId))
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
                _state.value = (_state.value ?: PlayerState()).copy(
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
                currentPosition = format(player.currentPosition)
            )
        } else {
            if (completed) {
                player.seekTo(0)
                completed = false
            }
            player.start()
            updateState(playbackState = PlayerState.PlaybackState.Playing)
            handler.post(updater)
        }
    }

    fun onFavouriteClicked() {
        val track = currentTrack ?: return

        viewModelScope.launch {
            val isFavourite = _state.value?.isFavourite ?: false

            if (isFavourite) {
                favoritesInteractor.removeTrack(track)
            } else {
                favoritesInteractor.addTrack(track)
            }

            updateFavourite(!isFavourite)
        }
    }

    fun release() {
        releasePlayer()
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    private fun releasePlayer() {
        stopUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
        prepared = false
        completed = false
    }

    private fun stopUpdates() {
        handler.removeCallbacks(updater)
    }

    private fun updateFavourite(isFavourite: Boolean) {
        _state.value = (_state.value ?: PlayerState()).copy(isFavourite = isFavourite)
    }

    private fun updateState(
        playbackState: PlayerState.PlaybackState,
        currentPosition: String = _state.value?.currentPosition ?: "00:00",
        isPlayButtonEnabled: Boolean = _state.value?.isPlayButtonEnabled ?: true
    ) {
        _state.value = (_state.value ?: PlayerState()).copy(
            playbackState = playbackState,
            currentPosition = currentPosition,
            isPlayButtonEnabled = isPlayButtonEnabled
        )
    }

    private fun format(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}