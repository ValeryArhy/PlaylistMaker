package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.mapper.toDomain
import com.example.playlistmaker.domain.api.TracksRepository

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String): List<Track> {
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            if (response.resultCode == 200 && response is TracksSearchResponse) {
                response.results.map { it.toDomain() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}