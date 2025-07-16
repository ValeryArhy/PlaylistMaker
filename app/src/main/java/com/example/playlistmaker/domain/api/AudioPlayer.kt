package com.example.playlistmaker.domain.api

interface AudioPlayer {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun seekToStart()
}