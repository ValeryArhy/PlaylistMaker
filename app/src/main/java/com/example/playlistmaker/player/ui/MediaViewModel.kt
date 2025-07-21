package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.usecase.TrackPlayerUseCase
import com.example.playlistmaker.app.formatTime


class MediaViewModel(
    private val playerUseCase: TrackPlayerUseCase
) : ViewModel() {

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _trackPosition = MutableLiveData("00:00")
    val trackPosition: LiveData<String> = _trackPosition

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimer = object : Runnable {
        override fun run() {
            _trackPosition.value = formatTime(playerUseCase.getPosition())
            handler.postDelayed(this, 200)
        }
    }

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerUseCase.prepare(url, {
            onPrepared()
            _trackPosition.value = "00:00"
        }, {
            _isPlaying.value = false
            _trackPosition.value = "00:00"
            handler.removeCallbacks(updateTimer)
            onCompletion()
        })
    }

    fun play() {
        playerUseCase.play()
        _isPlaying.value = true
        handler.post(updateTimer)
    }

    fun pause() {
        playerUseCase.pause()
        _isPlaying.value = false
        handler.removeCallbacks(updateTimer)
    }

    fun restart() {
        playerUseCase.restart()
        play()
    }

    fun togglePlayPause() {
        if (_isPlaying.value == true) pause() else play()
    }

    fun release() {
        playerUseCase.release()
        handler.removeCallbacks(updateTimer)
    }

}