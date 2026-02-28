package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.repository.SearchRepository
import java.net.UnknownHostException

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override suspend fun search(query: String): SearchInteractor.SearchResult {
        return try {
            val tracks = repository.search(query)
            SearchInteractor.SearchResult.Success(tracks)
        } catch (_: UnknownHostException) {
            SearchInteractor.SearchResult.NetworkError
        } catch (e: Exception) {
            SearchInteractor.SearchResult.Error(e)
        }
    }
}
