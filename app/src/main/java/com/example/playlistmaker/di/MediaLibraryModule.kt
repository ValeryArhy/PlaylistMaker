package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.PlaylistDbConverter
import com.example.playlistmaker.player.ui.FavoritesViewModel
import com.example.playlistmaker.player.ui.NewPlaylistViewModel
import com.example.playlistmaker.player.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {
    viewModel { FavoritesViewModel(get()) }
    viewModel { PlaylistsViewModel(get())}
    viewModel { NewPlaylistViewModel(get()) }

    single { PlaylistDbConverter() }
}