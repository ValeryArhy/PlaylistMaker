package com.example.playlistmaker.settings.domain.usecase

import com.example.playlistmaker.settings.domain.repository.ThemeRepository

class ThemeInteractor(private val themeRepository: ThemeRepository) {

    fun isDarkTheme(): Boolean {
        return themeRepository.isDarkTheme()
    }

    fun setDarkTheme(enabled: Boolean) {
        themeRepository.setDarkTheme(enabled)
    }
}