package com.example.playlistmaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.app.dpToPx
import com.example.playlistmaker.app.getCoverArtwork
import com.example.playlistmaker.app.getFormattedTime
import com.example.playlistmaker.app.getYearFromReleaseDate
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.domain.api.GetLastTrackUseCase
import com.example.playlistmaker.player.domain.api.SaveLastTrackUseCase
import com.example.playlistmaker.player.ui.MediaViewModel
import com.example.playlistmaker.search.domain.model.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

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

        track = arguments?.getSerializable("track") as? Track ?: getLastTrackUseCase.execute()

        if (track == null) {
            parentFragmentManager.popBackStack()
            return
        }

        setupUI(track!!)
        observeViewModel()
        viewModel.prepare(track!!.previewUrl ?: "", {}, {})
        saveLastTrackUseCase.execute(track!!)

        binding.play.setOnClickListener { viewModel.togglePlayPause() }
        binding.menuButton.setOnClickListener { parentFragmentManager.popBackStack() }
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

    companion object {
        fun newInstance(track: Track?): PlayerFragment = PlayerFragment().apply {
            arguments = Bundle().apply { putSerializable("track", track) }
        }
    }
}