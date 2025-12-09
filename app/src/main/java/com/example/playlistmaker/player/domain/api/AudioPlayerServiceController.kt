package com.example.playlistmaker.player.domain.api

import kotlinx.coroutines.flow.StateFlow

enum class PlayerState {
    IDLE, PREPARING, PLAYING, PAUSED, STOPPED
}

interface AudioPlayerServiceController {

    fun prepareAndPlay(trackUrl: String, trackName: String, artistName: String)
    fun togglePlayPause()
    fun pause()
    fun releasePlayer()


    fun startForegroundNotification(trackName: String, artistName: String)
    fun stopForegroundNotification(shouldStopService: Boolean = false)


    fun getPlayerStateFlow(): StateFlow<PlayerState>
    fun getTrackPositionFlow(): StateFlow<String>
}