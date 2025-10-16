package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.dpToPx
import com.example.playlistmaker.app.getCoverArtwork
import com.example.playlistmaker.app.getFormattedTime
import com.example.playlistmaker.app.getYearFromReleaseDate
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.domain.api.GetLastTrackUseCase
import com.example.playlistmaker.player.domain.api.SaveLastTrackUseCase
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    companion object {
        fun newInstance(track: Track?): PlayerFragment = PlayerFragment().apply {
            arguments = Bundle().apply { putParcelable("track", track) }
        }
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val getLastTrackUseCase: GetLastTrackUseCase by inject()
    private val saveLastTrackUseCase: SaveLastTrackUseCase by inject()
    private val viewModel: MediaViewModel by viewModel()

    private var track: Track? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            track = arguments?.getParcelable("track") ?: getLastTrackUseCase.execute()
            if (track == null) {
                parentFragmentManager.popBackStack()
                return@launch
            }

            setupUI(track!!)
            viewModel.setFavoriteState(track!!.isFavorite)
            observeViewModel()
            binding.favorites.setOnClickListener {
                track?.let { viewModel.onFavoriteClicked(it) }
            }
            viewModel.prepare(track!!.previewUrl ?: "", {}, {})
            saveLastTrackUseCase.execute(track!!)
        }

        binding.newPlaylist.setOnClickListener { findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)}
        binding.play.setOnClickListener { viewModel.togglePlayPause() }
        binding.menuButton.setOnClickListener { findNavController().popBackStack()
        }
    }

    private fun setupUI(track: Track) {
        binding.trackName.text = track.name
        binding.artistName.text = track.artist
        binding.albumAdapt.text = track.album
        binding.yearsAdapt.text = track.getYearFromReleaseDate()
        binding.genreAdapt.text = track.genre
        binding.countryAdapt.text = track.country
        binding.durationAdapt.text = track.getFormattedTime()

        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(requireContext().dpToPx(8)))
            .into(binding.iconAlbum)
    }

    private fun observeViewModel() {
        viewModel.isPlaying.observe(viewLifecycleOwner, Observer { isPlaying ->
            binding.play.isSelected = isPlaying
        })
        viewModel.trackPosition.observe(viewLifecycleOwner, Observer { time ->
            binding.trackTime.text = time
        })
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            binding.favorites.isSelected = isFav
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.release()
        _binding = null
    }
}