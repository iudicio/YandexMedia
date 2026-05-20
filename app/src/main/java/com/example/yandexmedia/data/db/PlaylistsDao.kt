package com.example.yandexmedia.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.yandexmedia.data.db.entity.PlaylistEntity
import com.example.yandexmedia.data.db.entity.PlaylistTrackCrossRef
import com.example.yandexmedia.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(
        playlist: PlaylistEntity
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(
        track: PlaylistTrackEntity
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(
        crossRef: PlaylistTrackCrossRef
    ): Long

    @Query("""
        UPDATE playlists
        SET tracksCount = tracksCount + 1
        WHERE id = :playlistId
    """)
    suspend fun incrementTracksCount(
        playlistId: Long
    )

    @Transaction
    suspend fun addTrackToPlaylist(
        playlistId: Long,
        track: PlaylistTrackEntity
    ): Boolean {

        insertTrack(track)

        val result = insertCrossRef(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = track.trackId
            )
        )

        val isAdded = result != -1L

        if (isAdded) {
            incrementTracksCount(playlistId)
        }

        return isAdded
    }
}