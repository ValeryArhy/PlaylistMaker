package com.example.playlistmaker.data.network

import android.util.Log
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TracksSearchRequest) {
                val resp = itunesService.searchTracks(dto.expression).execute()
                val body = resp.body() ?: Response()
                body.apply { resultCode = resp.code() }
            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: Exception) {
            Log.e("RetrofitNetworkClient", "Ошибка в doRequest", e)
            Response().apply { resultCode = -1 }
        }
    }
}