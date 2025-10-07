package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.search.presentation.SearchItem
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavoriteTracks()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter { track ->
            findNavController().navigate(
                R.id.playerFragment,
                bundleOf("track" to track)
            )
        }
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
        }
    }

    private fun observeFavoriteTracks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.favoriteTracks.collectLatest { tracks ->
                    if (tracks.isEmpty()) {
                        binding.favoritesRecyclerView.visibility = View.GONE
                        binding.placeholderImage.visibility = View.VISIBLE
                        binding.favoritesStub.visibility = View.VISIBLE
                    } else {
                        binding.favoritesRecyclerView.visibility = View.VISIBLE
                        binding.placeholderImage.visibility = View.GONE
                        binding.favoritesStub.visibility = View.GONE

                        val trackItems = tracks.map { SearchItem.TrackItem(it) }
                        adapter.submitList(trackItems)
                    }
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        fun newInstance(): FavoritesFragment{
            return FavoritesFragment()
        }
    }
}
