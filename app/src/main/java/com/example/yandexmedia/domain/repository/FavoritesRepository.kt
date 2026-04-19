package com.example.yandexmedia.domain.repository

import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    suspend fun isFavorite(trackId: Long): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>
}