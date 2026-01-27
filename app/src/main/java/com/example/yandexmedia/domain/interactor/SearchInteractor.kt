package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track

interface SearchInteractor {
    suspend fun search(query: String): List<Track>
}
