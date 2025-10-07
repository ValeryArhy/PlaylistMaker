package com.example.playlistmaker.player.data

import com.example.playlistmaker.app.db.TrackEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.id,
            trackName = track.name,
            artistName = track.artist,
            trackTimeMillis = track.durationMillis,
            artworkUrl100 = track.artworkUrl ?: "",
            collectionName = track.album ?: "",
            releaseDate = track.releaseYear ?: "",
            primaryGenreName = track.genre ?: "",
            country = track.country ?: "",
            previewUrl = track.previewUrl ?: ""
        )
    }

    fun map(entity: TrackEntity): Track {
        return Track(
            id = entity.trackId,
            name = entity.trackName,
            artist = entity.artistName,
            durationMillis = entity.trackTimeMillis,
            previewUrl = entity.previewUrl,
            album = entity.collectionName,
            releaseYear = entity.releaseDate.take(4),
            artworkUrl = entity.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"),
            genre = entity.primaryGenreName,
            country = entity.country,
            isFavorite = true
        )
    }
}