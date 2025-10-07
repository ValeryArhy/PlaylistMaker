package com.example.playlistmaker.di

import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.impl.FavoriteTracksInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

}