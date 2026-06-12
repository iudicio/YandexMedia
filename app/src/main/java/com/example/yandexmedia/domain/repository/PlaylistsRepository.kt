package com.example.yandexmedia.domain.repository

import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Long): Flow<Playlist?>

    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): Boolean

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ): Boolean

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun updatePlaylist(
        playlistId: Long,
        name: String,
        description: String,
        coverPath: String?
    )
}