package com.example.yandexmedia.presentation.ui.media.model

import com.example.yandexmedia.domain.model.Playlist

sealed interface PlaylistsState {
    data object Empty : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
}