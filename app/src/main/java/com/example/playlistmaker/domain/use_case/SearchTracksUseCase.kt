package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track

class SearchTracksUseCase(
    private val interactor: TracksInteractor
) {
    fun execute(expression: String, callback: (SearchResult) -> Unit) {
        interactor.searchTracks(expression, object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                callback(SearchResult.Success(tracks))
            }

            override fun onError(error: Throwable) {
                callback(SearchResult.Error)
            }
        })
    }
}