package com.example.playlistmaker.player.data

import com.example.playlistmaker.player.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean
}