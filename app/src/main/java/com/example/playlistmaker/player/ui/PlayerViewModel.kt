package com.example.playlistmaker.player.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.AudioPlayerServiceController
import com.example.playlistmaker.player.domain.api.PlayerState
import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.db.PlaylistInteractor
import com.example.playlistmaker.player.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch


class PlayerViewModel(

    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private var serviceController: AudioPlayerServiceController? = null

    private val _playerState = MutableLiveData(PlayerState.STOPPED)
    val playerState: LiveData<PlayerState> = _playerState

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


    private var currentTrack: Track? = null

    fun setFavoriteState(isFavorite: Boolean) {
        _isFavorite.value = isFavorite
    }

    fun setServiceController(controller: AudioPlayerServiceController, track: Track) {
        this.serviceController = controller
        this.currentTrack = track

        controller.prepareAndPlay(track.previewUrl ?: "", track.name, track.artist) // 👈 Только подготовка

        viewModelScope.launch {
            controller.getPlayerStateFlow().collect { state ->
                _playerState.postValue(state)

                _isPlaying.postValue(state == PlayerState.PLAYING)
            }
        }

        viewModelScope.launch {
            controller.getTrackPositionFlow().collect { position ->
                _trackPosition.postValue(position)
            }
        }
    }

    fun togglePlayPause() {
        serviceController?.togglePlayPause()
    }

    fun onUiVisible() {
        if (_playerState.value == PlayerState.PLAYING) {
            serviceController?.stopForegroundNotification()
        }
    }

    fun onUiHidden() {
        if (_playerState.value == PlayerState.PLAYING && currentTrack != null) {
            serviceController?.startForegroundNotification(currentTrack!!.name, currentTrack!!.artist)
        }
    }

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