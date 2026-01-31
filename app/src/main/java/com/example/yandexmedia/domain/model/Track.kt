package com.example.yandexmedia.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val previewUrl: String,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val trackTimeMillis: Long? = null
) : Parcelable {

    fun getCoverArtwork(): String =
        artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
