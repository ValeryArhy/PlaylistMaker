package com.example.playlistmaker


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presentation.SearchItem
import com.example.playlistmaker.search.presentation.SearchUiState
import com.example.playlistmaker.search.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isFirstLaunch = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearchInput()
        observeUiState()

        if (savedInstanceState != null) {
            val savedQuery = savedInstanceState.getString(SAVED_QUERY_KEY, "")
            val savedTracks =
                savedInstanceState.getParcelableArrayList<Track>(SAVED_TRACKS_KEY)

            if (!savedQuery.isNullOrEmpty() && !savedTracks.isNullOrEmpty()) {
                viewModel.restoreFromSavedState(savedQuery, savedTracks)
            } else {
                viewModel.restoreLastState()
            }
        } else {
            viewModel.restoreLastState()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstLaunch) {
            viewModel.restoreLastState()
        }
        isFirstLaunch = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, binding.queryInput.text?.toString().orEmpty())

        (viewModel.uiState.value as? SearchUiState.Content)?.let { contentState ->
            outState.putParcelableArrayList(SAVED_TRACKS_KEY, ArrayList(contentState.tracks))
        }
    }

    private fun setupRecyclerViews() {
        searchAdapter = TrackAdapter { track ->
            viewModel.saveTrackToHistory(track)
            findNavController().navigate(
                R.id.playerFragment,
                bundleOf("track" to track)
            )
            }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = searchAdapter

        historyAdapter = TrackAdapter { track ->
            viewModel.saveTrackToHistory(track)
            findNavController().navigate(
                R.id.playerFragment,
                bundleOf("track" to track)
            )
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SAVED_QUERY_KEY = "search_query"
        private const val SAVED_TRACKS_KEY = "saved_tracks"

        fun newInstance(): SearchFragment = SearchFragment()
    }
}