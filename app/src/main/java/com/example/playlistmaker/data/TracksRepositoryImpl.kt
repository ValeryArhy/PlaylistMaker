package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            if (response is TracksSearchResponse && response.resultCode == 200) {
                response.results.map { it.toDomain() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {

            emptyList()
        }
    }
}