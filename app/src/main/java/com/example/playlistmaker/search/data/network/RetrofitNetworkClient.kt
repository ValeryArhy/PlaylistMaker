package com.example.playlistmaker.search.data.network

import android.util.Log
import com.example.playlistmaker.search.data.Response
import com.example.playlistmaker.search.data.TracksSearchRequest

class RetrofitNetworkClient(private val itunesService: ItunesAPI) : NetworkClient {

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