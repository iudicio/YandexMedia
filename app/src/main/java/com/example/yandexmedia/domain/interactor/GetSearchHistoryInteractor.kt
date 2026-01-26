package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.SearchHistoryRepository

class GetSearchHistoryInteractor(
    private val repository: SearchHistoryRepository
) {
    fun execute(): List<Track> {
        return repository.getHistory()
    }
}
