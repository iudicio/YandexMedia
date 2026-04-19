package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        repository.removeTrack(track)
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return repository.isFavorite(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }
}