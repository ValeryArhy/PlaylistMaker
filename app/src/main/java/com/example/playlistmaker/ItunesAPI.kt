package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song ")
    fun searchMusic(
        @Query("term") text: String
    ): Call<SearchResponse>
}