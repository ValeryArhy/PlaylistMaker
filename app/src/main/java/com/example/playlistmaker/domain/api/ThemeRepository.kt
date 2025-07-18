package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}