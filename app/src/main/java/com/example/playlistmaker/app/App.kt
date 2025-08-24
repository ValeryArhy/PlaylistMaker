package com.example.playlistmaker.app

import android.app.Application
import com.example.playlistmaker.di.*
import com.example.playlistmaker.settings.domain.impl.ThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                searchModule,
                playerModule,
                settingsModule,
                mediaLibraryModule
            )
        }


        val themeInteractor: ThemeInteractor by inject(ThemeInteractor::class.java)
        val isDark = themeInteractor.isDarkTheme()

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}