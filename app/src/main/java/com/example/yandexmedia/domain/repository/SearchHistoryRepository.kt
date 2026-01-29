package com.example.yandexmedia.domain.repository

import com.example.yandexmedia.domain.model.Track

interface SearchHistoryRepository {
    fun read(): List<Track>
    fun write(tracks: List<Track>)
    fun clear()
}
