package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.db.PlaylistInteractor
import com.example.playlistmaker.player.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor,
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.value = interactor.getAllPlaylists()
        }
    }
}
