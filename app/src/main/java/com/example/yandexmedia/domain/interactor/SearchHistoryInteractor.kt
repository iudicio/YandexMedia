package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clear()
}
