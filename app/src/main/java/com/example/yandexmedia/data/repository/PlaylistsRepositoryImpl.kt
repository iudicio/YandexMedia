package com.example.yandexmedia.data.repository

import android.content.SharedPreferences
import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray
import org.json.JSONObject

class PlaylistsRepositoryImpl(
    private val prefs: SharedPreferences
) : PlaylistsRepository {

    private val playlistsFlow = MutableStateFlow(readPlaylists())

    override fun getPlaylists(): Flow<List<Playlist>> = playlistsFlow

    override suspend fun addPlaylist(playlist: Playlist) {
        val updated = listOf(playlist) + playlistsFlow.value
        savePlaylists(updated)
        playlistsFlow.value = updated
    }

    override suspend fun addTrackToPlaylist(
        playlistId: Long,
        trackId: Long
    ): Boolean {
        val playlists = playlistsFlow.value.toMutableList()
        val playlistIndex = playlists.indexOfFirst { it.id == playlistId }

        if (playlistIndex == -1) return false

        val playlist = playlists[playlistIndex]

        if (playlist.trackIds.contains(trackId)) {
            return false
        }

        val updatedPlaylist = playlist.copy(
            trackIds = playlist.trackIds + trackId,
            tracksCount = playlist.tracksCount + 1
        )

        playlists[playlistIndex] = updatedPlaylist

        savePlaylists(playlists)
        playlistsFlow.value = playlists

        return true
    }

    private fun readPlaylists(): List<Playlist> {
        val json = prefs.getString(KEY_PLAYLISTS, null) ?: return emptyList()
        val array = JSONArray(json)

        return List(array.length()) { index ->
            val obj = array.getJSONObject(index)
            val trackIdsJson = obj.optJSONArray("trackIds") ?: JSONArray()
            val trackIds = List(trackIdsJson.length()) { i ->
                trackIdsJson.getLong(i)
            }

            Playlist(
                id = obj.optLong("id"),
                name = obj.optString("name"),
                description = obj.optString("description"),
                coverPath = obj.optString("coverPath").takeIf { it.isNotBlank() },
                tracksCount = obj.optInt("tracksCount"),
                trackIds = trackIds
            )
        }
    }

    private fun savePlaylists(playlists: List<Playlist>) {
        val array = JSONArray()

        playlists.forEach { playlist ->
            array.put(
                JSONObject().apply {
                    put("id", playlist.id)
                    put("name", playlist.name)
                    put("description", playlist.description)
                    put("coverPath", playlist.coverPath)
                    put("tracksCount", playlist.tracksCount)
                    put("trackIds", JSONArray(playlist.trackIds))
                }
            )
        }

        prefs.edit()
            .putString(KEY_PLAYLISTS, array.toString())
            .apply()
    }

    private companion object {
        const val KEY_PLAYLISTS = "playlists"
    }
}