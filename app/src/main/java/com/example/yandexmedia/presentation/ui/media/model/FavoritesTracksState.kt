package com.example.yandexmedia.presentation.ui.media.model

import com.example.yandexmedia.domain.model.Track

sealed interface FavoritesTracksState {
    data object Empty : FavoritesTracksState
    data class Content(val tracks: List<Track>) : FavoritesTracksState
}