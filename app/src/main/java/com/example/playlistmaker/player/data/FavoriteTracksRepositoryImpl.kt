package com.example.playlistmaker.player.data

import com.example.playlistmaker.app.db.AppDatabase
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        val entity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(entity)
    }

    override suspend fun removeTrack(track: Track) {
        val entity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(entity)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao()
            .getAllFavoriteTracksFlow()
            .map { entities ->
                entities.map { trackDbConvertor.map(it) }
            }
    }
}