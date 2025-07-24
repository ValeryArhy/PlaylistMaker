package com.example.playlistmaker.search.ui
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.MediaActivity
import com.example.playlistmaker.search.presentation.SearchItem
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.presentation.SearchUiState
import com.example.playlistmaker.search.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.search.presentation.viewmodel.SearchViewModelFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(Creator.provideTracksInteractor())
    }
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchQuery: String = ""
    private val searchRunnable = Runnable { viewModel.searchTracks(latestSearchQuery) }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val SAVED_QUERY_KEY = "search_query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        setupSearchInput()
        setupObservers()

        val savedQuery = savedInstanceState?.getString(SAVED_QUERY_KEY).orEmpty()
        binding.queryInput.setText(savedQuery)
        latestSearchQuery = savedQuery
        viewModel.currentQuery = savedQuery

        if (savedQuery.isNotEmpty()) {
            viewModel.restoreLastState()
        } else {
            viewModel.loadHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.restoreLastState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, viewModel.currentQuery)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        searchAdapter = TrackAdapter { track ->
            viewModel.saveTrackToHistory(track)
            navigateToMediaActivity(track)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
        }

        historyAdapter = TrackAdapter { track ->
            navigateToMediaActivity(track)
        }
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }
    }

    private fun setupSearchInput() {
        binding.queryInput.doAfterTextChanged { text ->
            val query = text?.toString().orEmpty()
            binding.iconClear.isVisible = query.isNotEmpty()

            handler.removeCallbacks(searchRunnable)
            latestSearchQuery = query
            viewModel.currentQuery = query
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
        binding.iconClear.setOnClickListener {
            binding.queryInput.text?.clear()
        }
        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.updateButton.setOnClickListener {
            viewModel.retrySearch()
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            hideAll()

            when (state) {
                is SearchUiState.Loading -> binding.progressBar.isVisible = true

                is SearchUiState.Content -> {
                    searchAdapter.submitList(state.tracks.map { SearchItem.TrackItem(it) })
                    binding.recyclerView.isVisible = true
                }

                is SearchUiState.History -> {
                    if (state.tracks.isNotEmpty()) {
                        historyAdapter.submitList(state.tracks.map { SearchItem.TrackItem(it) })
                        binding.searchHistoryGroup.isVisible = true
                        binding.historyRecyclerView.isVisible = true
                        binding.clearHistory.isVisible = true
                    }
                }

                is SearchUiState.Empty -> {
                    binding.placeholderContainer.isVisible = true
                    binding.placeholderImage.isVisible = true
                    binding.noFound.isVisible = true
                }

                is SearchUiState.Error -> {
                    binding.placeholderContainer.isVisible = true
                    binding.placeholderImage.isVisible = false
                    binding.noFound.isVisible = false
                    binding.placeholderError.isVisible = true
                    binding.placeholderMessage.isVisible = true
                    binding.updateButton.isVisible = true
                }
            }
        }
    }

    private fun hideAll() {
        with(binding) {
            progressBar.isVisible = false
            recyclerView.isVisible = false
            searchHistoryGroup.isVisible = false
            historyRecyclerView.isVisible = false
            clearHistory.isVisible = false
            placeholderContainer.isVisible = false
        }
    }

    private fun navigateToMediaActivity(track: Track) {
        startActivity(
            Intent(this, MediaActivity::class.java)
                .putExtra("track", track)
        )
    }
}