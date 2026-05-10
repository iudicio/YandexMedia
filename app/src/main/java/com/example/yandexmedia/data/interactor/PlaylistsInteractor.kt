package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String, description: String, coverPath: String?)
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Boolean
}