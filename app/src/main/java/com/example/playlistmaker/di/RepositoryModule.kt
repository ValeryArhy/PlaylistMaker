package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.player.data.TrackDbConvertor
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory { TrackDbConvertor() }
}