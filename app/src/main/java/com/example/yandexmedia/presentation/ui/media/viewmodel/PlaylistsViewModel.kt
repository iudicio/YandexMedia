package com.example.yandexmedia.presentation.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.PlaylistsInteractor
import com.example.yandexmedia.presentation.ui.media.model.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> = _state

    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                _state.postValue(
                    if (playlists.isEmpty()) {
                        PlaylistsState.Empty
                    } else {
                        PlaylistsState.Content(playlists)
                    }
                )
            }
        }
    }
}