package com.example.yandexmedia.domain.repository

import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ): Boolean
}