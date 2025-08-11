package com.example.playlistmaker.di

import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.ThemeRepository
import com.example.playlistmaker.settings.domain.impl.ThemeInteractor
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val settingsModule = module {

    factory<ThemeRepository> { ThemeRepositoryImpl(get()) }


    factory { ThemeInteractor(get()) }


    viewModel { SettingsViewModel(get()) }
}