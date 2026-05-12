package com.example.yandexmedia.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yandexmedia.data.db.entity.FavoriteTrackEntity
import com.example.yandexmedia.data.db.entity.PlaylistEntity
import com.example.yandexmedia.data.db.entity.PlaylistTrackCrossRef
import com.example.yandexmedia.data.db.entity.PlaylistTrackEntity

@Database(
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        PlaylistTrackCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistsDao(): PlaylistsDao
}