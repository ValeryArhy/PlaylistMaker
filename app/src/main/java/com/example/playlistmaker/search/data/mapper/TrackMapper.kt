package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.TrackDto
import com.example.playlistmaker.search.domain.model.Track

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