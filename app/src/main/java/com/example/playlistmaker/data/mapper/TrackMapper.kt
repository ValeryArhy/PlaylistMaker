package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track

fun TrackDto.toDomain(): Track {
    return Track(
        id = trackId,
        name = trackName,
        artist = artistName,
        durationMillis = trackTimeMillis,
        previewUrl = previewUrl,
        album = collectionName,
        releaseYear = releaseDate.take(4),
        artworkUrl = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
        genre = primaryGenreName,
        country = country
    )
}