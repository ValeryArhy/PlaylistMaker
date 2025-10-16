package com.example.playlistmaker.player.data

import com.example.playlistmaker.app.db.PlaylistEntity
import com.example.playlistmaker.player.domain.model.Playlist


class PlaylistDbConverter {

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackCount = entity.trackCount
        )
    }

    fun mapToEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackCount = playlist.trackCount
        )
    }
}