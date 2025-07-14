package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.api.TracksInteractor

class SearchTracksUseCase(private val interactor: TracksInteractor) {
    fun execute(expression: String, consumer: TracksInteractor.TracksConsumer) {
        interactor.searchTracks(expression, consumer)
    }
}