package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.db.PlaylistInteractor
import com.example.playlistmaker.player.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun savePlaylist(
        name: String,
        description: String? = null,
        coverPath: String? = null,
        onSuccess: () -> Unit
    ) {
        val playlist = Playlist(
            id = 0L,
            name = name,
            description = description ?: "",
            coverPath = coverPath ?: "",
            trackCount = 0
        )

        viewModelScope.launch {
            playlistInteractor.createPlaylist(
                name = playlist.name,
                description = playlist.description,
                coverPath = playlist.coverPath
            )
            onSuccess()
        }
    }
}