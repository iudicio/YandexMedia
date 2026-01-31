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
    }

    override fun read(): List<Track> {
        val json = sharedPreferences.getString(KEY_HISTORY, null) ?: return emptyList()
        val jsonArray = JSONArray(json)

        val result = ArrayList<Track>(jsonArray.length())
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(jsonToTrack(obj))
        }
        return result
    }

    override fun write(tracks: List<Track>) {
        val array = JSONArray()
        tracks.forEach { array.put(trackToJson(it)) }

        sharedPreferences.edit()
            .putString(KEY_HISTORY, array.toString())
            .apply()
    }

    override fun clear() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply()
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
            collectionName = obj.optString("collectionName", ""),
            releaseDate = obj.optString("releaseDate", ""),
            primaryGenreName = obj.optString("primaryGenreName", ""),
            country = obj.optString("country", ""),
            trackTimeMillis = obj.optLong("trackTimeMillis")
        )
}
