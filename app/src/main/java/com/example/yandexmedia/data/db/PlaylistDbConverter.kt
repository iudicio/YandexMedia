package com.example.yandexmedia.data.db

import com.example.yandexmedia.data.db.entity.PlaylistEntity
import com.example.yandexmedia.data.db.entity.PlaylistTrackEntity
import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksCount = playlist.tracksCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            tracksCount = entity.tracksCount
        )
    }

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis
        )
    }

    fun map(entity: PlaylistTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = entity.trackTime,
            artworkUrl100 = entity.artworkUrl100,
            previewUrl = entity.previewUrl,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            trackTimeMillis = entity.trackTimeMillis
        )
    }
}