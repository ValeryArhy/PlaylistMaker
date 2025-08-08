package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.api.TrackPlayerUseCase

class MediaViewModelFactory(
    private val trackPlayerUseCase: TrackPlayerUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaViewModel(trackPlayerUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}