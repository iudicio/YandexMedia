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

    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Query(
        """
        SELECT playlist_tracks.* FROM playlist_tracks
        INNER JOIN playlist_track_cross_ref 
        ON playlist_tracks.trackId = playlist_track_cross_ref.trackId
        WHERE playlist_track_cross_ref.playlistId = :playlistId
        """
    )
    fun getTracksForPlaylist(playlistId: Long): Flow<List<PlaylistTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef): Long

    @Query(
        """
        UPDATE playlists
        SET tracksCount = tracksCount + 1
        WHERE id = :playlistId
        """
    )
    suspend fun incrementTracksCount(playlistId: Long)

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
    @Query(
        """
    DELETE FROM playlist_track_cross_ref
    WHERE playlistId = :playlistId AND trackId = :trackId
    """
    )
    suspend fun deleteTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): Int

    @Query(
        """
    UPDATE playlists
    SET tracksCount = CASE 
        WHEN tracksCount > 0 THEN tracksCount - 1 
        ELSE 0 
    END
    WHERE id = :playlistId
    """
    )
    suspend fun decrementTracksCount(playlistId: Long)

    @Transaction
    suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): Boolean {
        val deletedRows = deleteTrackFromPlaylist(
            playlistId = playlistId,
            trackId = trackId
        )

        val isDeleted = deletedRows > 0

        if (isDeleted) {
            decrementTracksCount(playlistId)
        }

        return isDeleted
    }
}