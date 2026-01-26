package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.SearchHistoryRepository

class AddTrackToHistoryInteractor(
    private val repository: SearchHistoryRepository
) {
    fun execute(track: Track) {
        repository.addTrack(track)
    }
}
