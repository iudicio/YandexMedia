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

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return dao.getPlaylistById(playlistId).map { entity ->
            entity?.let { converter.map(it) }
        }
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return dao.getTracksForPlaylist(playlistId).map { entities ->
            entities.map { converter.map(it) }
        }
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        dao.insertPlaylist(converter.map(playlist))
    }

    override suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): Boolean {
        return dao.removeTrackFromPlaylist(
            playlistId = playlistId,
            trackId = trackId
        )
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

    override suspend fun deletePlaylist(playlistId: Long) {
        dao.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        coverPath: String?
    ) {
        dao.updatePlaylist(
            playlistId = playlistId,
            name = name,
            description = description,
            coverPath = coverPath
        )
    }
}