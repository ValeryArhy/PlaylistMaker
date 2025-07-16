package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayer

class AudioPlayerImpl : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun seekToStart() {
        mediaPlayer?.seekTo(0)
    }
}