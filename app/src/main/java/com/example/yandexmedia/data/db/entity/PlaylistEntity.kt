package com.example.yandexmedia.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String?,
    val tracksCount: Int
)