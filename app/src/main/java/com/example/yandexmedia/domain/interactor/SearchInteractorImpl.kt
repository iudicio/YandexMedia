package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.net.UnknownHostException

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override fun search(query: String): Flow<SearchInteractor.SearchResult> {
        return repository.search(query)
            .map<List<Track>, SearchInteractor.SearchResult> { tracks ->
                SearchInteractor.SearchResult.Success(tracks)
            }
            .catch { throwable ->
                when (throwable) {
                    is UnknownHostException -> emit(SearchInteractor.SearchResult.NetworkError)
                    else -> emit(SearchInteractor.SearchResult.Error(throwable))
                }
            }
    }
}