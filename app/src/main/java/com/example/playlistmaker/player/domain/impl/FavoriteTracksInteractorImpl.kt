package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow


class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrack(track: Track) = favoriteTracksRepository.addTrack(track)
    override suspend fun removeTrack(track: Track) = favoriteTracksRepository.removeTrack(track)
    override fun favoriteTracks(): Flow<List<Track>> = favoriteTracksRepository.getAllFavoriteTracks()
}