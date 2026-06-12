package com.example.yandexmedia.presentation.ui.media.model

import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track

sealed interface PlaylistScreenState {
    data object Loading : PlaylistScreenState
    data object NotFound : PlaylistScreenState

    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>
    ) : PlaylistScreenState
}