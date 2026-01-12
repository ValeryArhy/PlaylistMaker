package com.example.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.impl.ThemeInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    init {
        _isDarkTheme.value = themeInteractor.isDarkTheme()
    }

    fun switchTheme(enabled: Boolean) {
        // 1. Сначала обновляем состояние во вьюмодели
        _isDarkTheme.value = enabled

        // 2. Сохраняем в настройки через интерактор
        themeInteractor.setDarkTheme(enabled)

    }
}