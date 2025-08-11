package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
    }
    single { Gson() }
}