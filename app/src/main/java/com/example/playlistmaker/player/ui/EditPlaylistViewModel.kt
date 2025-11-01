package com.example.playlistmaker.player.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Long
) : NewPlaylistViewModel(playlistInteractor) {

    val initialPlaylistName = MutableLiveData<String>()
    val initialDescription = MutableLiveData<String?>()
    val initialCoverPath = MutableLiveData<String?>()

    fun loadPlaylist() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlist?.let {
                initialPlaylistName.postValue(it.name)
                initialDescription.postValue(it.description)
                initialCoverPath.postValue(it.coverPath)
            }
        }
    }

    fun saveChanges(
        name: String,
        description: String?,
        coverPath: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val existingPlaylist = playlistInteractor.getPlaylistById(playlistId)
            if (existingPlaylist != null) {
                val updatedPlaylist = existingPlaylist.copy(
                    name = name,
                    description = description ?: existingPlaylist.description,
                    coverPath = coverPath ?: existingPlaylist.coverPath
                )
                playlistInteractor.updatePlaylist(updatedPlaylist)
                onSuccess()
            }
        }
    }
}