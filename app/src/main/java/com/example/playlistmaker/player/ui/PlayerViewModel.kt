package com.example.playlistmaker.player.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.TrackPlayerUseCase
import com.example.playlistmaker.app.formatTime
import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.db.PlaylistInteractor
import com.example.playlistmaker.player.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val playerUseCase: TrackPlayerUseCase,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _trackPosition = MutableLiveData("00:00")
    val trackPosition: LiveData<String> = _trackPosition

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _trackAddStatus = MutableLiveData<String?>()
    val trackAddStatus: LiveData<String?> get() = _trackAddStatus


    fun setFavoriteState(isFavorite: Boolean) {
        _isFavorite.value = isFavorite
    }

    private var progressJob: Job? = null
    private var lastPosition: Int = 0

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (_isFavorite.value == true) {
                favoriteTracksInteractor.removeTrack(track)
            } else {
                favoriteTracksInteractor.addTrack(track)
            }
            _isFavorite.postValue(!(_isFavorite.value ?: false))
        }
    }


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

    fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.value = interactor.getAllPlaylists()
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        if (playlist.trackIds.contains(track.id)) {
            _trackAddStatus.value = null
            return
        }

        viewModelScope.launch {
            val success = interactor.addTrackToPlaylist(track, playlist.id)
            if (success) {
                _trackAddStatus.postValue(playlist.name)
                _playlists.postValue(interactor.getAllPlaylists())
            } else {
                _trackAddStatus.postValue(null)
            }
        }
    }
}