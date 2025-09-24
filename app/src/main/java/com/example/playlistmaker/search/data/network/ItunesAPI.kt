package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song")
    suspend fun searchTracks(
        @Query("term") text: String
    ): TracksSearchResponse
}