package com.example.playlistmaker.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.SearchUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val interactor: TracksInteractor
) : ViewModel() {

    private val _uiState = MutableLiveData<SearchUiState>()
    val uiState: LiveData<SearchUiState> get() = _uiState

    private var searchJob: Job? = null
    private var currentQuery: String = ""


    fun onQueryChanged(query: String) {
        currentQuery = query.trim()

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            if (currentQuery.isEmpty()) {
                loadHistory()
            } else {
                searchTracks(currentQuery)
            }
        }
    }

    private fun searchTracks(query: String) {
        _uiState.value = SearchUiState.Loading

        interactor.searchTracks(query) { result ->
            result
                .onSuccess { tracks ->
                    _uiState.postValue(
                        if (tracks.isEmpty()) SearchUiState.Empty
                        else SearchUiState.Content(tracks)
                    )
                }
                .onFailure {
                    _uiState.postValue(SearchUiState.Error)
                }
        }
    }

    fun loadHistory() {
        interactor.loadHistory { result ->
            result
                .onSuccess { history ->
                    _uiState.postValue(SearchUiState.History(history))
                }
                .onFailure {
                    _uiState.postValue(SearchUiState.History(emptyList()))
                }
        }
    }

    fun saveTrackToHistory(track: Track) {
        interactor.saveTrack(track) {
            loadHistory()
        }
    }

    fun clearHistory() {
        interactor.clearHistory()
        _uiState.postValue(SearchUiState.History(emptyList()))
    }

    fun retrySearch() {
        if (currentQuery.isNotEmpty()) {
            searchTracks(currentQuery)
        }
    }

    fun restoreLastState() {
        if (currentQuery.isNotEmpty()) {
            searchTracks(currentQuery)
        } else {
            loadHistory()
        }
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}