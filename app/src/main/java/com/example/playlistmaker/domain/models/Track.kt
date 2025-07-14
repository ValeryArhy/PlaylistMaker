package com.example.playlistmaker.domain.models

import android.icu.text.SimpleDateFormat
import java.io.Serializable
import java.util.Locale

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

//    : Serializable {
//
//    fun getCoverArtwork(): String {
//        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
//    }
//
//    fun getFormattedTime(): String {
//        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
//        return dateFormat.format(trackTimeMillis)
//    }
//
//    fun getYearFromReleaseDate(): String {
//        return if (releaseDate.length >= 4) releaseDate.substring(0, 4) else ""
//    }
//}


