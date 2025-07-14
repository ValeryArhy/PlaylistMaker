package com.example.playlistmaker.ui
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.SearchViewModel
import com.example.playlistmaker.presentation.SearchViewModelFactory
import com.example.playlistmaker.ui.adapter.TrackAdapter


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(applicationContext)
    }

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchInput()
        setupObservers()

        viewModel.loadHistory()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.toolbar.title = getString(R.string.search)
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(
            onTrackClick = { track ->
                viewModel.saveTrackToHistory(track)
                navigateToMediaActivity(track)
            }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = TrackAdapter(
            onHistoryClick = { track ->
                navigateToMediaActivity(track)
            }
        )
        binding.historyRecyclerView.adapter = historyAdapter
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearchInput() {
        binding.queryInput.doAfterTextChanged { text ->
            val query = text?.toString() ?: ""
            viewModel.searchTracks(query)
            binding.iconClear.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
        }

        binding.iconClear.setOnClickListener {
            binding.queryInput.text?.clear()
        }
    }

    private fun setupObservers() {
        viewModel.tracks.observe(this) { trackItems ->
            val isHistory = trackItems.isNotEmpty() && trackItems.all { it.fromHistory }

            if (isHistory) {
                binding.searchHistoryGroup.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                binding.placeholderContainer.visibility = View.GONE
                binding.clearHistory.visibility = View.VISIBLE
                historyAdapter.submitList(trackItems)
            } else {
                binding.searchHistoryGroup.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.clearHistory.visibility = View.GONE
                adapter.submitList(trackItems)
            }
        }

        viewModel.isEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                binding.placeholderContainer.visibility = View.VISIBLE
                binding.placeholderImage.visibility = View.VISIBLE
                binding.noFound.visibility = View.VISIBLE
                binding.placeholderMessage.visibility = View.GONE
                binding.updateButton.visibility = View.GONE
            } else {
                binding.placeholderContainer.visibility = View.GONE
            }
        }

        viewModel.error.observe(this) { isError ->
            if (isError) {
                binding.placeholderContainer.visibility = View.VISIBLE
                binding.placeholderImage.visibility = View.GONE
                binding.noFound.visibility = View.GONE
                binding.placeholderMessage.visibility = View.VISIBLE
                binding.updateButton.visibility = View.VISIBLE
            } else {

                if (viewModel.isEmpty.value == false) {
                    binding.placeholderContainer.visibility = View.GONE
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.searchHistoryGroup.visibility = View.GONE
        }

        binding.updateButton.setOnClickListener {
            viewModel.searchTracks(binding.queryInput.text.toString())
        }
    }
    private fun navigateToMediaActivity(track: Track) {
        val intent = Intent(this, MediaActivity::class.java).apply {
            putExtra("track", track)
        }
        startActivity(intent)
    }
}

