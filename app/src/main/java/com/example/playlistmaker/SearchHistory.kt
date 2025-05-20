package com.example.playlistmaker

import android.content.SharedPreferences


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {
    private val type = object : TypeToken<MutableList<Track>>() {}.type

    fun saveTrack(track: Track) {
        val history = getHistory()

        history.removeAll { it.trackId == track.trackId }

        history.add(0, track)


        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }

        saveHistory(history)
    }

    fun getHistory(): MutableList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null)
        return if (!json.isNullOrEmpty()) {
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun clearHistory() {
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = Gson().toJson(history)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }


    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10

    }
}