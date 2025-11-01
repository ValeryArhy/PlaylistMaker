package com.example.playlistmaker.player.data


import com.example.playlistmaker.app.db.AppDatabase
import com.example.playlistmaker.app.db.PlaylistTrackEntity
import com.example.playlistmaker.player.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track


class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val converter: PlaylistDbConverter,
    private val trackConverter: TrackDbConvertor
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val entity = converter.mapToEntity(playlist)
        return appDatabase.playlistDao().insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = converter.mapToEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(entity)
    }

    override suspend fun getAllPlaylists(): List<Playlist> =
        appDatabase.playlistDao().getAllPlaylists().map { converter.map(it) }

    override suspend fun getPlaylistById(id: Long): Playlist? =
        appDatabase.playlistDao().getPlaylistById(id)?.let { converter.map(it) }

    override suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long> =
        appDatabase.playlistTrackDao().getTrackIdsForPlaylist(playlistId)

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {

        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: return false
        val playlist = converter.map(playlistEntity)

        if (playlist.trackIds.contains(track.id)) return false

        appDatabase.trackDao().insertTrackForPlaylist(trackConverter.map(track))
        appDatabase.playlistTrackDao().insert(PlaylistTrackEntity(track.id, playlistId))


        val updatedTrackIds = playlist.trackIds.toMutableList().apply { add(track.id) }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        appDatabase.playlistDao().updatePlaylist(converter.mapToEntity(updatedPlaylist))

        return true
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {

        appDatabase.playlistTrackDao().deleteTrackFromPlaylist(trackId, playlistId)


        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: return
        val playlist = converter.map(playlistEntity)
        val updatedTrackIds = playlist.trackIds.toMutableList().apply { remove(trackId) }
        appDatabase.playlistDao().updatePlaylist(
            converter.mapToEntity(playlist.copy(trackIds = updatedTrackIds, trackCount = updatedTrackIds.size))
        )


        val allTracks = appDatabase.playlistTrackDao().getAllPlaylistTracks()
        if (allTracks.none { it.trackId == trackId }) {
            appDatabase.trackDao().deleteTrackById(trackId)
        }
    }

    override suspend fun getTracksForPlaylist(trackIds: List<Long>): List<Track> =
        appDatabase.trackDao().getTracksByIds(trackIds).map { trackConverter.map(it) }

    override suspend fun getAllTracks(): List<Track> =
        appDatabase.trackDao().getAllTracks().map { trackConverter.map(it) }

    override suspend fun getTracksForIds(trackIds: List<Long>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()
        val entities = appDatabase.trackDao().getTracksByIds(trackIds)
        return entities.map { trackConverter.map(it) }
    }

    override suspend fun deletePlaylist(playlistId: Long) {

        val trackIds = appDatabase.playlistTrackDao().getTrackIdsForPlaylist(playlistId)
        trackIds.forEach { trackId ->
            appDatabase.playlistTrackDao().deleteTrackFromPlaylist(trackId, playlistId)
        }


        appDatabase.playlistDao().deletePlaylistById(playlistId)


        trackIds.forEach { trackId ->
            val allTracks = appDatabase.playlistTrackDao().getAllPlaylistTracks()
            if (allTracks.none { it.trackId == trackId }) {
                appDatabase.trackDao().deleteTrackById(trackId)
            }
        }
    }
}