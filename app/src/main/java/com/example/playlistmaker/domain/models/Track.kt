package com.example.playlistmaker.domain.models


import java.io.Serializable


data class Track(
    val id: Long,
    val name: String,
    val artist: String,
    val durationMillis: Long,
    val artworkUrl: String,
    val album: String,
    val releaseYear: String,
    val genre: String,
    val country: String,
    val previewUrl: String
) : Serializable