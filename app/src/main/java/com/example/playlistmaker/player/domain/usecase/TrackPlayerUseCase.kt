package com.example.playlistmaker.player.domain.usecase

import com.example.playlistmaker.player.api.AudioPlayer

class TrackPlayerUseCase(private val audioPlayer: AudioPlayer) {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) =
        audioPlayer.prepare(url, onPrepared, onCompletion)

    fun play() = audioPlayer.play()
    fun pause() = audioPlayer.pause()
    fun release() = audioPlayer.release()
    fun isPlaying() = audioPlayer.isPlaying()
    fun getPosition() = audioPlayer.getCurrentPosition()
    fun restart() = audioPlayer.seekToStart()
}