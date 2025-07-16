package com.example.playlistmaker.ui.settings

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App

class SettingsViewModel : ViewModel() {

    fun switchTheme(isDark: Boolean) {
        (App.instance).switchTheme(isDark)
    }
}