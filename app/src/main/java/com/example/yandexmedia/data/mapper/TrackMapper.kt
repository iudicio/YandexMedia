package com.example.yandexmedia.data.mapper

import com.example.yandexmedia.data.db.entity.FavoriteTrackEntity
import com.example.yandexmedia.domain.model.Track

fun Track.toEntity(): FavoriteTrackEntity {
    return FavoriteTrackEntity(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        previewUrl = previewUrl,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        trackTimeMillis = trackTimeMillis,
        addedAt = System.currentTimeMillis()
    )
}

fun FavoriteTrackEntity.toDomain(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        previewUrl = previewUrl,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        trackTimeMillis = trackTimeMillis
    )
}