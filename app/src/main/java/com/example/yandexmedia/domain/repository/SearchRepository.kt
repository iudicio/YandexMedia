package com.example.yandexmedia.domain.repository

import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: String): Flow<List<Track>>
}