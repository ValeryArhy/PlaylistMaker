package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackHistoryRepository(): TrackHistoryRepository {
        val sharedPrefs = appContext.getSharedPreferences("track_history", Context.MODE_PRIVATE)
        return TrackHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(
            provideTracksRepository(),
            provideTrackHistoryRepository()
        )
    }

    fun provideThemeRepository(): ThemeRepository {
        val sharedPrefs = appContext.getSharedPreferences("APP_THEME", Context.MODE_PRIVATE)
        return ThemeRepositoryImpl(sharedPrefs)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractor(provideThemeRepository())
    }
}