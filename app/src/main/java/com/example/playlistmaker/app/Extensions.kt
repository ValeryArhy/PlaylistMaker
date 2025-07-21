package com.example.playlistmaker.app

import android.content.Context
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Track.getCoverArtwork(): String {
    return artworkUrl.replaceAfterLast('/', "512x512bb.jpg")
}

fun Track.getFormattedTime(): String {
    val minutes = durationMillis / 60000
    val seconds = (durationMillis % 60000) / 1000
    return String.format("%d:%02d", minutes, seconds)
}

fun Track.getYearFromReleaseDate(): String {
    return if (releaseYear.length >= 4) releaseYear.substring(0, 4) else ""
}

fun formatTime(ms: Int): String {
    val format = SimpleDateFormat("mm:ss", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(ms)
}