package com.example.yandexmedia.data.repository

import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SearchRepositoryImpl : SearchRepository {

    override suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = URL(
            "https://itunes.apple.com/search" +
                    "?entity=song&attribute=songTerm&limit=25&term=$encodedQuery"
        )

        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 1000
        connection.readTimeout = 1000

        try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                parseTracks(response)
            } else {
                emptyList()
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseTracks(json: String): List<Track> {
        val result = mutableListOf<Track>()
        val results = JSONObject(json).getJSONArray("results")

        for (i in 0 until results.length()) {
            val item = results.getJSONObject(i)
            val timeMillis = item.optLong("trackTimeMillis", 0)

            result.add(
                Track(
                    trackId = item.optLong("trackId", 0),
                    trackName = item.optString("trackName", "Без названия"),
                    artistName = item.optString("artistName", "Неизвестен"),
                    trackTime = millisToTime(timeMillis),
                    artworkUrl100 = item.optString("artworkUrl100", ""),
                    previewUrl = item.optString("previewUrl", ""),
                    collectionName = item.optString("collectionName", ""),
                    releaseDate = item.optString("releaseDate", ""),
                    primaryGenreName = item.optString("primaryGenreName", ""),
                    country = item.optString("country", ""),
                    trackTimeMillis = timeMillis
                )
            )
        }
        return result
    }

    private fun millisToTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
