package com.example.playlistmaker.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.SearchItem
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.use_case.ClearTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.GetTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.SaveTrackToHistoryUseCase
import com.example.playlistmaker.domain.use_case.SearchTracksUseCase

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getTrackHistoryUseCase: GetTrackHistoryUseCase,
    private val saveTrackToHistoryUseCase: SaveTrackToHistoryUseCase,
    private val clearTrackHistoryUseCase: ClearTrackHistoryUseCase
) : ViewModel() {

    private val _tracks = MutableLiveData<List<SearchItem.TrackItem>>()
    val tracks: LiveData<List<SearchItem.TrackItem>> = _tracks

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    private val _error = MutableLiveData<Boolean>(false)
    val error: LiveData<Boolean> = _error

    fun searchTracks(query: String) {
        if (query.isBlank()) {
            loadHistory()
            return
        }

        _isLoading.value = true
        _error.value = false
        _isEmpty.value = false

        searchTracksUseCase.execute(query, object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                _isLoading.postValue(false)
                val items = tracks.map { SearchItem.TrackItem(it, fromHistory = false) }
                _tracks.postValue(items)
                _isEmpty.postValue(items.isEmpty())
            }

            override fun onError(error: Throwable) {
                _isLoading.postValue(false)
                _error.postValue(true)
                _isEmpty.postValue(false)
            }
        })
    }

    fun loadHistory() {
        val history = getTrackHistoryUseCase.execute()
        val items = history.map { SearchItem.TrackItem(it, fromHistory = true) }
        _tracks.value = items
    }

    fun saveTrackToHistory(track: Track) {
        saveTrackToHistoryUseCase.execute(track)
    }

    fun clearHistory() {
        clearTrackHistoryUseCase.execute()
        _tracks.value = emptyList()
    }
}
