package com.example.playlistmaker.search.data

import com.google.gson.annotations.SerializedName

data class TracksSearchResponse(
    @SerializedName("resultCount")
    val resultCount: Int,

    @SerializedName("results")
    val results: List<TrackDto>
)