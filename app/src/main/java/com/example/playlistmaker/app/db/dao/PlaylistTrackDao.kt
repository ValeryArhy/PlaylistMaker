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
}