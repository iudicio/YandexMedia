package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    suspend fun isFavorite(trackId: Long): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>
}