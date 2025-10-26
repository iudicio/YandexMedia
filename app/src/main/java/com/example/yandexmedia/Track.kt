package com.example.yandexmedia

data class Track(
    val trackName: String,      // Название композиции
    val artistName: String,     // Исполнитель
    val trackTime: String,      // Продолжительность
    val artworkUrl100: String   // Ссылка на обложку
)
