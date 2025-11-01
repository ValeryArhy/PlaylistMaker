package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.player.data.PlaylistRepository
import com.example.playlistmaker.player.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track

class PlaylistInteractor(
    private val repository: PlaylistRepository
) {


    suspend fun createPlaylist(
        name: String,
        description: String? = null,
        coverPath: String? = null
    ): Long {
        val playlist = Playlist(
            id = 0L,
            name = name,
            description = description,
            coverPath = coverPath,
            trackCount = 0
        )
        return repository.createPlaylist(playlist)
    }


    suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }


    suspend fun getAllPlaylists(): List<Playlist> {
        return repository.getAllPlaylists()
    }


    suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }


    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {
        return repository.addTrackToPlaylist(track, playlistId)
    }


    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        repository.deleteTrackFromPlaylist(playlistId, trackId)
    }


    suspend fun getTracksByIds(trackIds: List<Long>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()
        return repository.getTracksForIds(trackIds)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }
}


