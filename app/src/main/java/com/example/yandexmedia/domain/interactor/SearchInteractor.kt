package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track

interface SearchInteractor {
    suspend fun search(query: String): SearchResult

    sealed interface SearchResult {
        data class Success(val tracks: List<Track>) : SearchResult
        data object NetworkError : SearchResult
        data class Error(val throwable: Throwable? = null) : SearchResult
    }
}
