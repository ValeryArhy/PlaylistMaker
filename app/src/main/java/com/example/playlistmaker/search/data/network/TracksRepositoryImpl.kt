package com.example.playlistmaker.search.data.network

import android.util.Log
import com.example.playlistmaker.search.data.TracksSearchRequest
import com.example.playlistmaker.search.data.TracksSearchResponse
import com.example.playlistmaker.search.data.mapper.toDomain
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.model.SearchResult


class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
) : TracksRepository {

    override fun searchTracks(expression: String): SearchResult {
        Log.d("TracksRepo", "Поиск по запросу: $expression")
        return try {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            Log.d("TracksRepo", "Ответ сети получен: resultCode = ${response.resultCode}")
            if (response.resultCode == 200 && response is TracksSearchResponse) {
                Log.d("TracksRepo", "Успешно: ${response.results.size} результатов")
                SearchResult.Success(response.results.map { it.toDomain() })
            } else {
                Log.e("TracksRepo", "Ошибка ответа: ${response.resultCode}")
                SearchResult.Error
            }
        } catch (e: Exception) {
            Log.e("TracksRepo", "Исключение при выполнении запроса", e)
            SearchResult.Error
        }
    }
}