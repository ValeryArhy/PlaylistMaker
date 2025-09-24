package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.api.AudioPlayer

class AudioPlayerImpl(
    private val mediaPlayerFactory: () -> MediaPlayer
) : AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    private enum class State { IDLE, PREPARING, PLAYING, PAUSED }
    private var state = State.IDLE

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        if (mediaPlayer == null) {
            mediaPlayer = mediaPlayerFactory()
        }

        mediaPlayer?.apply {
            reset()
            setDataSource(url)
            setOnPreparedListener {
                state = State.PAUSED
                onPrepared()
            }
            setOnCompletionListener {
                state = State.PAUSED
                onCompletion()
            }
            prepareAsync()
            state = State.PREPARING
        }
    }

    override fun play() {
        mediaPlayer?.let {
            if (state == State.PAUSED || state == State.PREPARING) {
                it.start()
                state = State.PLAYING
            }
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            if (state == State.PLAYING) {
                it.pause()
                state = State.PAUSED
            }
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        state = State.IDLE
    }

    override fun isPlaying(): Boolean = state == State.PLAYING

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun seekToStart() {
        mediaPlayer?.seekTo(0)
        state = State.PAUSED
    }
}