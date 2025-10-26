package com.example.playlistmaker.player.data


import android.util.Log
import com.example.playlistmaker.app.db.AppDatabase
import com.example.playlistmaker.app.db.PlaylistTrackEntity
import com.example.playlistmaker.player.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: PlaylistDbConverter,
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val entity = converter.mapToEntity(playlist)
        return appDatabase.playlistDao().insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = converter.mapToEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(entity)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return appDatabase.playlistDao()
            .getAllPlaylists()
            .map { converter.map(it) }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return appDatabase.playlistDao()
            .getPlaylistById(id)
            ?.let { converter.map(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {

        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: return false

        val playlist = converter.map(playlistEntity)

        if (playlist.trackIds.contains(track.id)) {
            return false
        }


        val playlistTrackEntity = PlaylistTrackEntity(trackId = track.id, playlistId = playlistId)
        appDatabase.playlistTrackDao().insert(playlistTrackEntity)


        val updatedTrackIds = playlist.trackIds.toMutableList().apply { add(track.id) }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )


        val updatedPlaylistEntity = converter.mapToEntity(updatedPlaylist)
        appDatabase.playlistDao().updatePlaylist(updatedPlaylistEntity)

        return true
    }

}