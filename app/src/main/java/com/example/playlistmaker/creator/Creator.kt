package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.api.AudioPlayer
import com.example.playlistmaker.player.data.AudioPlayerImpl
import com.example.playlistmaker.player.domain.api.GetLastTrackUseCase
import com.example.playlistmaker.player.domain.api.SaveLastTrackUseCase
import com.example.playlistmaker.player.domain.api.TrackPlayerUseCase
import com.example.playlistmaker.player.domain.impl.GetLastTrackUseCaseImpl
import com.example.playlistmaker.player.domain.impl.SaveLastTrackUseCaseImpl
import com.example.playlistmaker.player.domain.impl.TrackPlayerUseCaseImpl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.domain.usecase.ThemeInteractor
import com.example.playlistmaker.settings.domain.repository.ThemeRepository
import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl

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

    fun provideAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }

    fun provideGetLastTrackUseCase(): GetLastTrackUseCase {
        return GetLastTrackUseCaseImpl(provideTrackHistoryRepository())
    }

    fun provideSaveLastTrackUseCase(): SaveLastTrackUseCase {
        return SaveLastTrackUseCaseImpl(provideTrackHistoryRepository())
    }

    fun provideTrackPlayerUseCase(): TrackPlayerUseCase {
        return TrackPlayerUseCaseImpl(provideAudioPlayer())
    }

    fun provideThemeRepository(): ThemeRepository {
        val sharedPrefs = appContext.getSharedPreferences("APP_THEME", Context.MODE_PRIVATE)
        return ThemeRepositoryImpl(sharedPrefs)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractor(provideThemeRepository())
    }
}