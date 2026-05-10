package com.example.yandexmedia.presentation.ui.media.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.PlaylistsInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

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
}