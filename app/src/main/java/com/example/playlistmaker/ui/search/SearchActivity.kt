package com.example.playlistmaker.ui.search
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.SearchItem
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.adapter.TrackAdapter
import com.example.playlistmaker.ui.player.MediaActivity


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels { SearchViewModelFactory() }

    // Два адаптера: для поиска и для истории
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchQuery: String = ""
    private val searchRunnable = Runnable { viewModel.searchTracks(latestSearchQuery) }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        setupSearchInput()
        setupObservers()

        viewModel.loadHistory()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        // Адаптер для поиска
        searchAdapter = TrackAdapter { track ->
            viewModel.saveTrackToHistory(track)
            navigateToMediaActivity(track)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
        }

        // Адаптер для истории
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
                is SearchUiState.Loading -> {
                    binding.progressBar.isVisible = true
                }
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