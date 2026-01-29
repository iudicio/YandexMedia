package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track

class AddTrackToHistoryInteractor(
    private val historyInteractor: SearchHistoryInteractor
) {
    fun execute(track: Track) {
        historyInteractor.addTrack(track)
    }
}
