package com.example.yandexmedia.presentation.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.PlaylistsInteractor
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.ui.media.model.PlaylistScreenState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var currentPlaylistId: Long = 0L

    private val _state = MutableLiveData<PlaylistScreenState>(PlaylistScreenState.Loading)
    val state: LiveData<PlaylistScreenState> = _state

    fun loadPlaylist(playlistId: Long) {
        currentPlaylistId = playlistId

        viewModelScope.launch {
            combine(
                playlistsInteractor.getPlaylistById(playlistId),
                playlistsInteractor.getTracksForPlaylist(playlistId)
            ) { playlist, tracks ->
                if (playlist == null) {
                    PlaylistScreenState.NotFound
                } else {
                    PlaylistScreenState.Content(
                        playlist = playlist,
                        tracks = tracks
                    )
                }
            }.collect { screenState ->
                _state.postValue(screenState)
            }
        }
    }

    fun removeTrackFromPlaylist(
        track: Track,
        onResult: () -> Unit
    ) {
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(
                playlistId = currentPlaylistId,
                trackId = track.trackId
            )

            onResult()
        }
    }

    fun deletePlaylist(onResult: () -> Unit) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(currentPlaylistId)
            onResult()
        }
    }
}