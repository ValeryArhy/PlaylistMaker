package com.example.playlistmaker.app.db

import androidx.room.Entity

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["trackId", "playlistId"]
)
data class PlaylistTrackEntity(
    val trackId: Long,
    val playlistId: Long
)