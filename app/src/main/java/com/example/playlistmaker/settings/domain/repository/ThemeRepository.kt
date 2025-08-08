package com.example.playlistmaker.settings.domain.repository

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}