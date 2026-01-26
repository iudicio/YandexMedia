package com.example.yandexmedia.domain.repository

import com.example.yandexmedia.domain.model.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
