package com.example.yandexmedia.domain.repository

import com.example.yandexmedia.domain.model.Track

interface SearchRepository {
    suspend fun search(query: String): List<Track>
}
