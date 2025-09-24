package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.TracksSearchResponse
import com.example.playlistmaker.search.data.mapper.toDomain
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class TracksRepositoryImpl(
    private val api: ItunesAPI
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<List<Track>> = flow {
        val response: TracksSearchResponse = api.searchTracks(expression)
        val tracks = response.results.map { it.toDomain() }
        emit(tracks)
    }.flowOn(Dispatchers.IO)
}