package com.example.playlistmaker.player.data


import com.example.playlistmaker.app.db.AppDatabase
import com.example.playlistmaker.player.domain.model.Playlist

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: PlaylistDbConverter
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
}