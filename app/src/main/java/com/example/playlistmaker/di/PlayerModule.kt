package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.api.AudioPlayer
import com.example.playlistmaker.player.data.AudioPlayerImpl
import com.example.playlistmaker.player.domain.api.GetLastTrackUseCase
import com.example.playlistmaker.player.domain.api.SaveLastTrackUseCase
import com.example.playlistmaker.player.domain.api.TrackPlayerUseCase
import com.example.playlistmaker.player.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.player.domain.impl.GetLastTrackUseCaseImpl
import com.example.playlistmaker.player.domain.impl.SaveLastTrackUseCaseImpl
import com.example.playlistmaker.player.domain.impl.TrackPlayerUseCaseImpl
import com.example.playlistmaker.player.ui.EditPlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.player.ui.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory { { MediaPlayer() } }
    single<AudioPlayer> { AudioPlayerImpl(get()) }

    factory<GetLastTrackUseCase> { GetLastTrackUseCaseImpl(get()) }
    factory<SaveLastTrackUseCase> { SaveLastTrackUseCaseImpl(get()) }
    factory<TrackPlayerUseCase> { TrackPlayerUseCaseImpl(get()) }

    single<FavoriteTracksInteractor> { FavoriteTracksInteractorImpl(get()) }

    viewModel { PlayerViewModel(get(), get(), get()) }
    viewModel { PlaylistViewModel(get()) }
    viewModel { EditPlaylistViewModel(get(),get()) }

}