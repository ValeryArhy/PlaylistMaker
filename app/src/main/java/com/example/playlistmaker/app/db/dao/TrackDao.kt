package com.example.playlistmaker.app.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.app.db.TrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT trackId FROM tracks_table")
    suspend fun getAllFavoriteTrackId(): List<Long>

    @Query("SELECT * FROM tracks_table ORDER BY addedAt DESC")
    fun getAllFavoriteTracksFlow(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks_table WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<Long>): List<TrackEntity>

    @Query("DELETE FROM tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("SELECT * FROM tracks_table")
    suspend fun getAllTracks(): List<TrackEntity>

}