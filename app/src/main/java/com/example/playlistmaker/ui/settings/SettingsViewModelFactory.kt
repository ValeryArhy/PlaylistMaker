package com.example.playlistmaker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.ThemeInteractor

class SettingsViewModelFactory(
    private val themeInteractor: ThemeInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(themeInteractor) as T
    }
}