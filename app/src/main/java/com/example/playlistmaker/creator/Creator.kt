package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.presentation.SearchViewModel
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
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

    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackHistoryRepository(context: Context): TrackHistoryRepository {
        val sharedPrefs = context.getSharedPreferences("track_history", Context.MODE_PRIVATE)
        return TrackHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(
            provideTracksRepository(),
            provideTrackHistoryRepository(context)
        )
    }

    fun provideSearchTracksUseCase(context: Context): SearchTracksUseCase {
        return SearchTracksUseCase(provideTracksInteractor(context))
    }

    fun provideGetTrackHistoryUseCase(context: Context): GetTrackHistoryUseCase {
        return GetTrackHistoryUseCase(provideTrackHistoryRepository(context))
    }

    fun provideSaveTrackToHistoryUseCase(context: Context): SaveTrackToHistoryUseCase {
        return SaveTrackToHistoryUseCase(provideTrackHistoryRepository(context))
    }

    fun provideClearTrackHistoryUseCase(context: Context): ClearTrackHistoryUseCase {
        return ClearTrackHistoryUseCase(provideTrackHistoryRepository(context))
    }

    fun provideSearchViewModel(context: Context): SearchViewModel {
        return SearchViewModel(
            provideSearchTracksUseCase(context),
            provideGetTrackHistoryUseCase(context),
            provideSaveTrackToHistoryUseCase(context),
            provideClearTrackHistoryUseCase(context)
        )
    }
}
