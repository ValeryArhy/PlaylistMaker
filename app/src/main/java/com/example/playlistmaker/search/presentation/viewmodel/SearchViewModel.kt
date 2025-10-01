package com.example.playlistmaker.search.presentation.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.SearchUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

class SearchViewModel(
    private val interactor: TracksInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _uiState = MutableLiveData<SearchUiState>()
    val uiState: LiveData<SearchUiState> get() = _uiState

    private var searchJob: Job? = null
    private var currentQuery: String = ""
    private var lastFoundTracks: List<Track>? = null

    val latestQuery: String get() = currentQuery
    val latestTracks: List<Track>? get() = lastFoundTracks

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

    private suspend fun searchTracks(query: String) {
        _uiState.value = SearchUiState.Loading
        interactor.searchTracks(query)
            .catch { _uiState.postValue(SearchUiState.Error) }
            .collectLatest { tracks ->
                lastFoundTracks = tracks
                _uiState.postValue(
                    if (tracks.isEmpty()) SearchUiState.Empty
                    else SearchUiState.Content(tracks)
                )
            }
    }

    private suspend fun loadHistory() {
        interactor.loadHistory()
            .catch { _uiState.postValue(SearchUiState.History(emptyList())) }
            .collectLatest { history ->
                _uiState.postValue(SearchUiState.History(history))
            }
    }

    fun saveTrackToHistory(track: Track) {
        viewModelScope.launch {
            interactor.saveTrack(track)
            loadHistory()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            interactor.clearHistory()
            _uiState.postValue(SearchUiState.History(emptyList()))
        }
    }

    fun retrySearch() {
        if (currentQuery.isNotEmpty()) {
            viewModelScope.launch { searchTracks(currentQuery) }
        }
    }

    fun restoreLastState() {
        when {
            !lastFoundTracks.isNullOrEmpty() -> {
                _uiState.value = SearchUiState.Content(lastFoundTracks!!)
            }
            currentQuery.isNotEmpty() -> {
                viewModelScope.launch { searchTracks(currentQuery) }
            }
            else -> {
                viewModelScope.launch { loadHistory() }
            }
        }
    }

    fun restoreFromSavedState(query: String, tracks: List<Track>) {
        currentQuery = query
        lastFoundTracks = tracks
        _uiState.value = SearchUiState.Content(tracks)
    }
}