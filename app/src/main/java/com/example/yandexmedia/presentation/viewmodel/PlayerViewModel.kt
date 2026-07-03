package com.example.yandexmedia.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.FavoritesInteractor
import com.example.yandexmedia.domain.interactor.PlaylistsInteractor
import kotlinx.coroutines.launch
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.player.PlaybackState
import com.example.yandexmedia.player.PlayerServiceContract
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

class PlayerViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableLiveData(PlayerState())
    val state: LiveData<PlayerState> = _state

    val playlists = playlistsInteractor.getPlaylists().asLiveData()

    private var currentTrack: Track? = null
    private var playerService: PlayerServiceContract? = null
    private var serviceStateJob: Job? = null
    fun addTrackToPlaylist(
        playlistId: Long,
        track: Track,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val isAdded = playlistsInteractor.addTrackToPlaylist(
                playlistId = playlistId,
                track = track
            )
            onResult(isAdded)
        }
    }
    fun prepare(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            updateFavourite(favoritesInteractor.isFavorite(track.trackId))
        }
        playerService?.prepare(track)
    }

    fun onPlayPause() {
        playerService?.playPause()
    }

    fun onServiceConnected(service: PlayerServiceContract, track: Track) {
        playerService = service
        prepare(track)
        serviceStateJob?.cancel()
        serviceStateJob = viewModelScope.launch {
            service.playbackState.collectLatest(::applyPlaybackState)
        }
    }

    fun onUiForegrounded() = playerService?.hideForegroundNotification()

    fun onUiBackgrounded(notificationsAllowed: Boolean) {
        if (notificationsAllowed && _state.value?.playbackState == PlayerState.PlaybackState.Playing) {
            playerService?.showForegroundNotification()
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
        serviceStateJob?.cancel()
        serviceStateJob = null
        playerService?.releasePlayer()
        playerService = null
    }

    override fun onCleared() {
        release()
        super.onCleared()
    }

    private fun applyPlaybackState(playback: PlaybackState) {
        val status = when (playback.status) {
            PlaybackState.Status.IDLE -> PlayerState.PlaybackState.Idle
            PlaybackState.Status.PREPARED -> PlayerState.PlaybackState.Prepared
            PlaybackState.Status.PLAYING -> PlayerState.PlaybackState.Playing
            PlaybackState.Status.PAUSED -> PlayerState.PlaybackState.Paused
            PlaybackState.Status.COMPLETED -> PlayerState.PlaybackState.Completed
            PlaybackState.Status.ERROR -> PlayerState.PlaybackState.Error
        }
        _state.value = (_state.value ?: PlayerState()).copy(
            playbackState = status,
            currentPosition = format(playback.currentPositionMillis),
            isPlayButtonEnabled = playback.isPlayButtonEnabled,
            error = playback.error
        )
    }

    private fun updateFavourite(isFavourite: Boolean) {
        _state.value = (_state.value ?: PlayerState()).copy(isFavourite = isFavourite)
    }

    private fun format(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
