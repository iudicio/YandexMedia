package com.example.yandexmedia.data.repository

import android.content.SharedPreferences
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.SearchHistoryRepository
import org.json.JSONArray
import org.json.JSONObject

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SearchHistoryRepository {

    companion object {
        private const val KEY_HISTORY = "search_history"
        private const val MAX_SIZE = 10
    }

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(KEY_HISTORY, null) ?: return emptyList()
        val jsonArray = JSONArray(json)
        val result = mutableListOf<Track>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val track = jsonToTrack(obj)

            if (track.previewUrl.isNotBlank()) {
                result.add(track)
            }
        }

        if (result.size != jsonArray.length()) {
            saveHistory(result)
        }

        return result
    }

    override fun addTrack(track: Track) {
        val list = getHistory().toMutableList()

        list.removeAll {
            it.trackId == track.trackId
        }

        list.add(0, track)

        if (list.size > MAX_SIZE) {
            list.subList(MAX_SIZE, list.size).clear()
        }

        saveHistory(list)
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(list: List<Track>) {
        val array = JSONArray()
        list.forEach { array.put(trackToJson(it)) }

        sharedPreferences.edit()
            .putString(KEY_HISTORY, array.toString())
            .apply()
    }

    private fun trackToJson(track: Track): JSONObject =
        JSONObject().apply {
            put("trackId", track.trackId)
            put("trackName", track.trackName)
            put("artistName", track.artistName)
            put("trackTime", track.trackTime)
            put("artworkUrl100", track.artworkUrl100)
            put("previewUrl", track.previewUrl)
            put("collectionName", track.collectionName)
            put("releaseDate", track.releaseDate)
            put("primaryGenreName", track.primaryGenreName)
            put("country", track.country)
            put("trackTimeMillis", track.trackTimeMillis)
        }

    private fun jsonToTrack(obj: JSONObject): Track =
        Track(
            trackId = obj.optLong("trackId"),
            trackName = obj.optString("trackName"),
            artistName = obj.optString("artistName"),
            trackTime = obj.optString("trackTime"),
            artworkUrl100 = obj.optString("artworkUrl100"),
            previewUrl = obj.optString("previewUrl"),
            collectionName = obj.optString("collectionName", null),
            releaseDate = obj.optString("releaseDate", null),
            primaryGenreName = obj.optString("primaryGenreName", null),
            country = obj.optString("country", null),
            trackTimeMillis = obj.optLong("trackTimeMillis")
        )
}
