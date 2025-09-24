package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.repository.TrackHistoryRepository
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackHistoryRepositoryImpl(

    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : TrackHistoryRepository {

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val LAST_PLAYED_KEY = "last_played_track"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val type = object : TypeToken<MutableList<Track>>() {}.type


    override suspend fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.id == track.id }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }
        saveHistory(history)
    }

    override suspend fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    override suspend fun clearHistory() {
        sharedPrefs.edit().remove(SEARCH_HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    override suspend fun saveLastPlayedTrack(track: Track) {
        val json = gson.toJson(track)
        sharedPrefs.edit()
            .putString(LAST_PLAYED_KEY, json)
            .apply()
    }

    override suspend  fun getLastPlayedTrack(): Track? {
        val json = sharedPrefs.getString(LAST_PLAYED_KEY, null)
        return json?.let { gson.fromJson(it, Track::class.java) }
    }

}