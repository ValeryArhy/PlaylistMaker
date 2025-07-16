package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.use_case.ClearTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.GetTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.SaveTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.SearchTracksUseCase

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

}