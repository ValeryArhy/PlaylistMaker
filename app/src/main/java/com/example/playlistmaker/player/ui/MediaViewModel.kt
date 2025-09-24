package com.example.playlistmaker.player.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.TrackPlayerUseCase
import com.example.playlistmaker.app.formatTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MediaViewModel(
    private val playerUseCase: TrackPlayerUseCase
) : ViewModel() {

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _trackPosition = MutableLiveData("00:00")
    val trackPosition: LiveData<String> = _trackPosition

    private var progressJob: Job? = null
    private var lastPosition: Int = 0

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerUseCase.prepare(url, {
            onPrepared()
            _trackPosition.value = "00:00"
        }, {
            _isPlaying.value = false
            _trackPosition.value = "00:00"
            stopProgressUpdates()
            onCompletion()
        })
    }

    fun play() {
        playerUseCase.play()
        _isPlaying.value = true
        startProgressUpdates()
    }

    fun pause() {
        lastPosition = playerUseCase.getPosition()
        playerUseCase.pause()
        _isPlaying.value = false
        stopProgressUpdates()
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
        stopProgressUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        playerUseCase.release()
        stopProgressUpdates()
    }


    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = viewModelScope.launch {
            while (isActive) {
                _trackPosition.value = formatTime(playerUseCase.getPosition())
                delay(300L)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }
}