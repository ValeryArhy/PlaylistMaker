package com.example.playlistmaker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.use_case.TrackPlayerUseCase

class MediaViewModelFactory(
    private val trackPlayerUseCase: TrackPlayerUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MediaViewModel(trackPlayerUseCase) as T
    }
}