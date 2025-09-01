package com.example.playlistmaker.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable