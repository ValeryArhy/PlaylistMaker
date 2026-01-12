package com.example.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.*
import com.example.playlistmaker.settings.domain.impl.ThemeInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                appModule, searchModule, playerModule, settingsModule,
                mediaLibraryModule, dataModule, repositoryModule, interactorModule
            )
        }

        // Получаем Interactor через Koin после инициализации
        val themeInteractor: ThemeInteractor = getKoin().get()

        // Применяем тему при запуске
        switchTheme(themeInteractor.isDarkTheme())
    }

    // Выносим в отдельный метод, чтобы логика была прозрачной
    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}