package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun search(query: String): Flow<SearchResult>

    sealed interface SearchResult {
        data class Success(val tracks: List<Track>) : SearchResult
        data object NetworkError : SearchResult
        data class Error(val throwable: Throwable? = null) : SearchResult
    }
}