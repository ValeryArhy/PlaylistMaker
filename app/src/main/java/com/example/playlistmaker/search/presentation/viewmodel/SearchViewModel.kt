package com.example.playlistmaker.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.SearchUiState

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
                _uiState.value = SearchUiState.History(emptyList())
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