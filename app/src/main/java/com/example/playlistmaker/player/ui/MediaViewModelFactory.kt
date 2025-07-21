package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.usecase.TrackPlayerUseCase

class MediaViewModelFactory(
    private val trackPlayerUseCase: TrackPlayerUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MediaViewModel(trackPlayerUseCase) as T
    }
}