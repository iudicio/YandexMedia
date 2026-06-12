package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return repository.getPlaylistById(playlistId)
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return repository.getTracksForPlaylist(playlistId)
    }

    override suspend fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?
    ) {
        repository.addPlaylist(
            Playlist(
                id = System.currentTimeMillis(),
                name = name,
                description = description,
                coverPath = coverPath,
                tracksCount = 0
            )
        )
    }

    override suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): Boolean {
        return repository.removeTrackFromPlaylist(
            playlistId = playlistId,
            trackId = trackId
        )
    }

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ): Boolean {
        return repository.addTrackToPlaylist(
            playlistId = playlistId,
            track = track
        )
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        coverPath: String?
    ) {
        repository.updatePlaylist(
            playlistId = playlistId,
            name = name,
            description = description,
            coverPath = coverPath
        )
    }
}