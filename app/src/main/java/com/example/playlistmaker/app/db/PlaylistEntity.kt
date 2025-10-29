package com.example.playlistmaker.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val trackIds: String = "[]",
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackCount: Int = 0
)
