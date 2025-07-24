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

    var currentQuery: String = ""  // делаем var, чтобы можно было обновлять из SearchActivity

    fun searchTracks(query: String) {
        currentQuery = query.trim()

        if (currentQuery.isEmpty()) {
            loadHistory()
            return
        }

        _uiState.postValue(SearchUiState.Loading)

        interactor.searchTracks(currentQuery) { result ->
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
        interactor.saveTrack(track)
        loadHistory()
    }

    fun clearHistory() {
        interactor.clearHistory()
        _uiState.postValue(SearchUiState.History(emptyList()))
    }

    fun retrySearch() {
        searchTracks(currentQuery)
    }

    // Вот этот метод вызывай из SearchActivity при восстановлении экрана
    fun restoreLastState() {
        if (currentQuery.isNotEmpty()) {
            searchTracks(currentQuery)
        } else {
            loadHistory()
        }
    }
}