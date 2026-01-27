package com.example.yandexmedia.data.interactor

import com.example.yandexmedia.data.repository.SearchRepository
import com.example.yandexmedia.domain.interactor.SearchInteractor
import com.example.yandexmedia.domain.model.Track

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override suspend fun search(query: String): List<Track> {
        return repository.search(query)
    }
}
