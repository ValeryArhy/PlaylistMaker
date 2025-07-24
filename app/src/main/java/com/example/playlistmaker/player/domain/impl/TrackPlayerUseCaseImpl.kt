package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.api.AudioPlayer
import com.example.playlistmaker.player.domain.api.TrackPlayerUseCase

class TrackPlayerUseCaseImpl(
    private val audioPlayer: AudioPlayer
) : TrackPlayerUseCase {
    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        audioPlayer.prepare(url, onPrepared, onCompletion)
    }

    override fun play() = audioPlayer.play()
    override fun pause() = audioPlayer.pause()
    override fun release() = audioPlayer.release()
    override fun isPlaying() = audioPlayer.isPlaying()
    override fun getPosition() = audioPlayer.getCurrentPosition()
    override fun restart() = audioPlayer.seekToStart()
}