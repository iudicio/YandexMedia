package com.example.yandexmedia.data.interactor

import com.example.yandexmedia.data.domain.SearchHistory
import com.example.yandexmedia.domain.interactor.SearchHistoryInteractor
import com.example.yandexmedia.domain.model.Track

class SearchHistoryInteractorImpl(
    private val storage: SearchHistory
) : SearchHistoryInteractor {

    override fun getHistory(): List<Track> =
        storage.getHistory()

    override fun addTrack(track: Track) {
        storage.addTrack(track)
    }

    override fun clear() {
        storage.clear()
    }
}
