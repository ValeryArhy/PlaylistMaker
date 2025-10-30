package com.example.playlistmaker.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.app.db.PlaylistTrackEntity


@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: PlaylistTrackEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_tracks WHERE trackId = :trackId AND playlistId = :playlistId)")
    suspend fun isTrackInPlaylist(trackId: Long, playlistId: Long): Boolean

    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)

    @Query("SELECT * FROM playlist_tracks")
    suspend fun getAllPlaylistTracks(): List<PlaylistTrackEntity>
}