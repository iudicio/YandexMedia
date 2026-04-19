package com.example.yandexmedia.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yandexmedia.data.db.entity.FavoriteTrackEntity

@Database(
    entities = [FavoriteTrackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao
}