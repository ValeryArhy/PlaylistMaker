package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.usecase.ThemeInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    init {
        _isDarkTheme.value = themeInteractor.isDarkTheme()
    }

    fun switchTheme(enabled: Boolean) {
        themeInteractor.setDarkTheme(enabled)
        _isDarkTheme.value = enabled
    }
}