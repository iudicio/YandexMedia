package com.example.yandexmedia

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_HISTORY = "search_history"
        private const val MAX_SIZE = 10
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(KEY_HISTORY, null) ?: return emptyList()
        val result = mutableListOf<Track>()
        val jsonArray = JSONArray(json)

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


    fun addTrack(track: Track) {
        val list = getHistory().toMutableList()

        list.removeAll {
            it.trackName == track.trackName &&
                    it.artistName == track.artistName &&
                    it.trackTime == track.trackTime &&
                    it.artworkUrl100 == track.artworkUrl100
        }

        list.add(0, track)

        if (list.size > MAX_SIZE) {
            list.subList(MAX_SIZE, list.size).clear()
        }

        saveHistory(list)
    }

    fun clear() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(list: List<Track>) {
        val array = JSONArray()
        list.forEach { array.put(trackToJson(it)) }
        sharedPreferences.edit()
            .putString(KEY_HISTORY, array.toString())
            .apply()
    }

    private fun trackToJson(track: Track): JSONObject {
        return JSONObject().apply {
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
    }


    private fun jsonToTrack(obj: JSONObject): Track {
        return Track(
            trackId = obj.optLong("trackId", 0),
            trackName = obj.optString("trackName", "Без названия"),
            artistName = obj.optString("artistName", "Неизвестен"),
            trackTime = obj.optString("trackTime", "0:00"),
            artworkUrl100 = obj.optString("artworkUrl100", ""),
            previewUrl = obj.optString("previewUrl", ""),
            collectionName = obj.optString("collectionName", null),
            releaseDate = obj.optString("releaseDate", null),
            primaryGenreName = obj.optString("primaryGenreName", null),
            country = obj.optString("country", null),
            trackTimeMillis = if (obj.has("trackTimeMillis")) obj.optLong("trackTimeMillis") else null
        )
    }


}
