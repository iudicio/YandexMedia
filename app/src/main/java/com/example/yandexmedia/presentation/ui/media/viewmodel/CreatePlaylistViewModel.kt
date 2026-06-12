package com.example.yandexmedia.presentation.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.PlaylistsInteractor
import com.example.yandexmedia.domain.model.Playlist
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> = _playlist

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            _playlist.postValue(
                playlistsInteractor.getPlaylistById(playlistId).first()
            )
        }
    }

    fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?,
        onCreated: () -> Unit
    ) {
        viewModelScope.launch {
            playlistsInteractor.createPlaylist(
                name = name,
                description = description,
                coverPath = coverPath
            )
            onCreated()
        }
    }

    fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        coverPath: String?,
        onUpdated: () -> Unit
    ) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(
                playlistId = playlistId,
                name = name,
                description = description,
                coverPath = coverPath
            )
            onUpdated()
        }
    }
}