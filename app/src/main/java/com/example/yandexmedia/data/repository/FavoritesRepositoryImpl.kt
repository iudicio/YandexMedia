package com.example.yandexmedia.data.repository

import com.example.yandexmedia.data.db.FavoriteTrackDao
import com.example.yandexmedia.data.db.TrackDbConverter
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoriteTrackDao,
    private val converter: TrackDbConverter
) : FavoritesRepository {

    override suspend fun addTrack(track: Track) {
        dao.insertTrack(converter.map(track))
    }

    override suspend fun removeTrack(track: Track) {
        dao.deleteTrack(converter.map(track))
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return dao.isFavorite(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getTracks().map { entities ->
            entities.map { converter.map(it) }
        }
    }
}