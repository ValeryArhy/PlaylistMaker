package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.search.presentation.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.playlistmaker.search.presentation.SearchUiState

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

                PlaylistMakerTheme {
                val uiState by viewModel.uiState.observeAsState(SearchUiState.History(emptyList()))
                var searchText by remember { mutableStateOf(viewModel.latestQuery) }


                    SearchScreen(
                        uiState = uiState,
                        query = searchText,
                        onQueryChange = { text ->
                            searchText = text
                            viewModel.onQueryChanged(text)
                        },
                        onTrackClick = { track ->
                            viewModel.saveTrackToHistory(track)
                            findNavController().navigate(
                                R.id.action_searchFragment_to_playerFragment,
                                bundleOf("track" to track)
                            )
                        },
                        onClearHistory = { viewModel.clearHistory() },
                        onRetry = { viewModel.retrySearch() },
                        onClearQuery = {
                            searchText = ""
                            viewModel.onQueryChanged("")
                        }
                    )
                }
            }
        }
    }
}