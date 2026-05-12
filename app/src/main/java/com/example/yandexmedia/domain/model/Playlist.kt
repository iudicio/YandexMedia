package com.example.yandexmedia.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String?,
    val tracksCount: Int
)