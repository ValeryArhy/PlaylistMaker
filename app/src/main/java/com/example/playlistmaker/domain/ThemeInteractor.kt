package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractor(private val themeRepository: ThemeRepository) {

    fun isDarkTheme(): Boolean {
        return themeRepository.isDarkTheme()
    }

    fun setDarkTheme(enabled: Boolean) {
        themeRepository.setDarkTheme(enabled)
    }
}