package com.example.yandexmedia.presentation.viewmodel

import com.example.yandexmedia.domain.model.Track

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data object Empty : SearchState
    data object NetworkError : SearchState
}
