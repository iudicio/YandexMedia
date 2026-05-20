package com.example.yandexmedia.data.repository

import com.example.yandexmedia.data.db.PlaylistDbConverter
import com.example.yandexmedia.data.db.PlaylistsDao
import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val dao: PlaylistsDao,
    private val converter: PlaylistDbConverter
) : PlaylistsRepository {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return dao.getPlaylists().map { entities ->
            entities.map { converter.map(it) }
        }
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        dao.insertPlaylist(converter.map(playlist))
    }

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ): Boolean {

        return dao.addTrackToPlaylist(
            playlistId = playlistId,
            track = converter.map(track)
        )
    }
}