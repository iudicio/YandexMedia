package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track

class GetSearchHistoryInteractor(
    private val historyInteractor: SearchHistoryInteractor
) {
    fun execute(): List<Track> {
        return historyInteractor.getHistory()
    }
}
