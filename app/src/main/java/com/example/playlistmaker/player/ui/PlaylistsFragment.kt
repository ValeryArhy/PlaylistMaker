package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.player.ui.adapter.PlaylistsAdapter

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var adapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistsAdapter()
        binding.rvPlaylists.adapter = adapter
        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                binding.rvPlaylists.visibility = View.GONE
                binding.placeholderImage.visibility = View.VISIBLE
                binding.playlistsStub.visibility = View.VISIBLE
            } else {
                binding.rvPlaylists.visibility = View.VISIBLE
                binding.placeholderImage.visibility = View.GONE
                binding.playlistsStub.visibility = View.GONE
                adapter.setItems(playlists)
            }
        }


        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                viewModel.loadPlaylists()
            }
        })

        binding.newPlaylistButton.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}