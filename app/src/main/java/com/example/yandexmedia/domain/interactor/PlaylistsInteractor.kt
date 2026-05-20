package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Long): Flow<Playlist?>

    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): Boolean

    suspend fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?
    )

    suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: Track
    ): Boolean
}