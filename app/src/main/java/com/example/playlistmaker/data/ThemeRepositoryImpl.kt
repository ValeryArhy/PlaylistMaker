package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    companion object {
        private const val KEY_DARK_THEME = "DARK_THEME"
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }
}