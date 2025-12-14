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
            result.add(jsonToTrack(obj))
        }
        return result
    }

    fun addTrack(track: Track) {
        val list = getHistory().toMutableList()

        // убираем дубликат (если уже был в истории)
        list.removeAll {
            it.trackName == track.trackName &&
                    it.artistName == track.artistName &&
                    it.trackTime == track.trackTime &&
                    it.artworkUrl100 == track.artworkUrl100
        }

        // новый (или обновлённый) трек в начало
        list.add(0, track)

        // обрезаем до 10 элементов
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
        val obj = JSONObject()
        obj.put("trackId", track.trackId)
        obj.put("trackName", track.trackName)
        obj.put("artistName", track.artistName)
        obj.put("trackTime", track.trackTime)
        obj.put("artworkUrl100", track.artworkUrl100)

        obj.put("collectionName", track.collectionName)
        obj.put("releaseDate", track.releaseDate)
        obj.put("primaryGenreName", track.primaryGenreName)
        obj.put("country", track.country)
        obj.put("trackTimeMillis", track.trackTimeMillis)

        return obj
    }

    private fun jsonToTrack(obj: JSONObject): Track {
        return Track(
            trackId = obj.optLong("trackId", 0),
            trackName = obj.optString("trackName", "Без названия"),
            artistName = obj.optString("artistName", "Неизвестен"),
            trackTime = obj.optString("trackTime", "0:00"),
            artworkUrl100 = obj.optString("artworkUrl100", ""),
            collectionName = obj.optString("collectionName", null),
            releaseDate = obj.optString("releaseDate", null),
            primaryGenreName = obj.optString("primaryGenreName", null),
            country = obj.optString("country", null),
            trackTimeMillis = if (obj.has("trackTimeMillis")) obj.optLong("trackTimeMillis") else null
        )
    }

}
