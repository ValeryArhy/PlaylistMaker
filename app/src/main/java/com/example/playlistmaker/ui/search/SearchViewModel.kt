package com.example.playlistmaker.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.SearchItem
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.use_case.ClearTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.GetTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.SaveTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.SearchTracksUseCase

class SearchViewModel(
    private val interactor: TracksInteractor
) : ViewModel() {

    private val _uiState = MutableLiveData<SearchUiState>()
    val uiState: LiveData<SearchUiState> = _uiState

    private var currentQuery: String = ""

    fun searchTracks(query: String) {
        currentQuery = query.trim()

        if (currentQuery.isEmpty()) {
            loadHistory()
            return
        }

        _uiState.value = SearchUiState.Loading

        interactor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                _uiState.value = if (tracks.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Content(tracks)
                }
            }

            override fun onError(error: Throwable) {
                _uiState.value = SearchUiState.Error
            }
        })
    }

    fun loadHistory() {
        interactor.loadHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                _uiState.value = SearchUiState.History(tracks)
            }

            override fun onError(error: Throwable) {
                _uiState.value = SearchUiState.History(emptyList()) // или Error, если важно
            }
        })
    }

    fun saveTrackToHistory(track: Track) {
        interactor.saveTrack(track)
        loadHistory()
    }

    fun clearHistory() {
        interactor.clearHistory()
        _uiState.value = SearchUiState.History(emptyList())
    }

    fun retrySearch() {
        searchTracks(currentQuery)
    }
}