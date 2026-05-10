package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
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
                tracksCount = 0,
                trackIds = emptyList()
            )
        )
    }

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        trackId: Long
    ): Boolean {
        return repository.addTrackToPlaylist(playlistId, trackId)
    }
}