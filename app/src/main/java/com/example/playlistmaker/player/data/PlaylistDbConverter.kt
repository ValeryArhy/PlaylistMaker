package com.example.playlistmaker.player.data

import com.example.playlistmaker.app.db.PlaylistEntity
import com.example.playlistmaker.player.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PlaylistDbConverter {

    private val gson = Gson()

    fun map(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        val ids: List<Long> = gson.fromJson(entity.trackIds, type) ?: emptyList()
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = ids,
            trackCount = ids.size
        )
    }

    fun mapToEntity(playlist: Playlist): PlaylistEntity {
        val json = gson.toJson(playlist.trackIds)
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = json,
            trackCount = playlist.trackIds.size
        )
    }
}