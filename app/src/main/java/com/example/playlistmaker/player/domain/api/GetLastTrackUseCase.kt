package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.model.Track

interface GetLastTrackUseCase {
    fun execute(): Track?
}