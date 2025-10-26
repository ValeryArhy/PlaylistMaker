package com.example.playlistmaker.player.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String? = null,
    val coverPath: String? = null,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
)