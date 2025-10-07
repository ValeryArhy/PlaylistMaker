package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface FavoriteTracksInteractor {
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    fun favoriteTracks(): Flow<List<Track>>
}