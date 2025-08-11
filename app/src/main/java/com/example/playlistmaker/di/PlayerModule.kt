package com.example.playlistmaker.di

import com.example.playlistmaker.player.api.AudioPlayer
import com.example.playlistmaker.player.data.AudioPlayerImpl
import com.example.playlistmaker.player.domain.api.GetLastTrackUseCase
import com.example.playlistmaker.player.domain.api.SaveLastTrackUseCase
import com.example.playlistmaker.player.domain.api.TrackPlayerUseCase
import com.example.playlistmaker.player.domain.impl.GetLastTrackUseCaseImpl
import com.example.playlistmaker.player.domain.impl.SaveLastTrackUseCaseImpl
import com.example.playlistmaker.player.domain.impl.TrackPlayerUseCaseImpl
import com.example.playlistmaker.player.ui.MediaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    single<AudioPlayer> { AudioPlayerImpl() }

    single<GetLastTrackUseCase> { GetLastTrackUseCaseImpl(get()) }
    single<SaveLastTrackUseCase> { SaveLastTrackUseCaseImpl(get()) }
    single<TrackPlayerUseCase> { TrackPlayerUseCaseImpl(get()) }

    viewModel { MediaViewModel(get()) }
}