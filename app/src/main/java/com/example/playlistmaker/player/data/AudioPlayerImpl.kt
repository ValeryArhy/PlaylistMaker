package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.api.AudioPlayer

class AudioPlayerImpl(
    private val mediaPlayerFactory: () -> MediaPlayer
) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer?.release()
        mediaPlayer = mediaPlayerFactory().apply {
            setDataSource(url)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
            prepareAsync()
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