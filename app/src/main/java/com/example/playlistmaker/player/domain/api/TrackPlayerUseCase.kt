package com.example.playlistmaker.player.domain.api

interface TrackPlayerUseCase {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getPosition(): Int
    fun restart()
}