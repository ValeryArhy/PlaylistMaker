package com.example.playlistmaker.search.ui
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.MediaActivity
import com.example.playlistmaker.search.presentation.SearchItem
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.presentation.SearchUiState
import com.example.playlistmaker.search.presentation.viewmodel.SearchViewModel


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    companion object {
        private const val SAVED_QUERY_KEY = "search_query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        setupSearchInput()
        observeUiState()

        savedInstanceState?.getString(SAVED_QUERY_KEY)?.let {
            binding.queryInput.setText(it)
            viewModel.onQueryChanged(it)
        } ?: viewModel.restoreLastState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, binding.queryInput.text?.toString().orEmpty())
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        searchAdapter = TrackAdapter { track ->
            viewModel.saveTrackToHistory(track)
            navigateToMediaActivity(track)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = searchAdapter

        historyAdapter = TrackAdapter { track ->
            navigateToMediaActivity(track)
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupSearchInput() {
        binding.queryInput.doAfterTextChanged { text ->
            val query = text?.toString().orEmpty()
            binding.iconClear.isVisible = query.isNotEmpty()
            viewModel.onQueryChanged(query)
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

    private fun observeUiState() {
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
            placeholderImage.isVisible = false
            placeholderError.isVisible = false
            placeholderMessage.isVisible = false
            updateButton.isVisible = false
            noFound.isVisible = false
        }
    }

    private fun navigateToMediaActivity(track: Track) {
        startActivity(Intent(this, MediaActivity::class.java).putExtra("track", track))
    }
}