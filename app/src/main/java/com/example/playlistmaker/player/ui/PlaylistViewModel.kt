package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.db.PlaylistInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch


class PlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _playlistName = MutableLiveData<String>()
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData<String?>()
    val playlistDescription: LiveData<String?> = _playlistDescription

    private val _trackCount = MutableLiveData<Int>()
    val trackCount: LiveData<Int> = _trackCount

    private val _isEmpty = MutableLiveData(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    private val _playlistCover = MutableLiveData<String?>()
    val playlistCover: LiveData<String?> = _playlistCover

    private val _playlistId = MutableLiveData<Long>()
    val playlistId: LiveData<Long> = _playlistId

    val totalMinutes = MediatorLiveData<String>().apply {
        addSource(_tracks) { trackList ->
            val durationSumMillis = trackList.sumOf { it.durationMillis }
            val minutes = durationSumMillis / 1000 / 60
            value = "$minutes минут"
        }
    }

    val trackCountText = MediatorLiveData<String>().apply {
        addSource(_trackCount) { count ->
            val remainder100 = count % 100
            val remainder10 = count % 10
            val text = when {
                remainder100 in 11..14 -> "треков"
                remainder10 == 1 -> "трек"
                remainder10 in 2..4 -> "трека"
                else -> "треков"
            }
            value = "$count $text"
        }
    }

    fun loadPlaylistTracks(playlistId: Long) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            if (playlist == null) {
                _tracks.postValue(emptyList())
                _playlistName.postValue("")
                _playlistDescription.postValue("")
                _playlistCover.postValue(null)
                _trackCount.postValue(0)
                _isEmpty.postValue(true)
                return@launch
            }

            _playlistName.postValue(playlist.name)
            _playlistDescription.postValue(playlist.description)
            _playlistCover.postValue(playlist.coverPath)

            val trackIds = playlist.trackIds
            val tracksList = if (trackIds.isNotEmpty()) {
                interactor.getTracksByIds(trackIds)
            } else emptyList()

            _trackCount.postValue(tracksList.size)
            _tracks.postValue(tracksList)
            _isEmpty.postValue(tracksList.isEmpty())
        }
    }

    fun deleteTrack(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            interactor.deleteTrackFromPlaylist(playlistId, trackId)
            loadPlaylistTracks(playlistId)
        }
    }
    fun deletePlaylist(playlistId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            interactor.deletePlaylist(playlistId)
            onDeleted()
        }
    }
}