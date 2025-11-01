package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.playlistmaker.player.ui.adapter.PlaylistAdapterMini
import com.example.playlistmaker.search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapterMini: PlaylistAdapterMini

    private val getLastTrackUseCase: GetLastTrackUseCase by inject()
    private val saveLastTrackUseCase: SaveLastTrackUseCase by inject()
    private val viewModel: PlayerViewModel by viewModel()

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener("newPlaylistCreated", this) { _, bundle ->
            val playlistId = bundle.getLong("new_playlist_id")
            viewModel.loadPlaylists()

            track?.let { currentTrack ->
                viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
                    playlists.find { it.id == playlistId }?.let { newPlaylist ->
                        viewModel.addTrackToPlaylist(currentTrack, newPlaylist)
                    }
                }
            }
        }
    }

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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.newPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED,
                    BottomSheetBehavior.STATE_EXPANDED,
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.overlay.alpha = 0.5f
                    }
                    else -> binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset.coerceAtLeast(0f) * 0.5f)
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                isEnabled = false
                requireActivity()
            }
        }

        binding.menuButton.setOnClickListener {
                findNavController().popBackStack()
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.play.setOnClickListener { viewModel.togglePlayPause() }

        adapterMini = PlaylistAdapterMini { playlist ->
            track?.let { viewModel.addTrackToPlaylist(it, playlist) }
        }

        binding.rvPlaylistMini.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylistMini.adapter = adapterMini

        viewModel.playlists.observe(viewLifecycleOwner) { list ->
            adapterMini.setItems(list)
        }
        viewModel.loadPlaylists()

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
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
        viewModel.trackAddStatus.observe(viewLifecycleOwner) { playlistName ->
            playlistName?.let { name ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_added_to_playlist, name),
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.loadPlaylists()
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_already_in_playlist),
                    Toast.LENGTH_SHORT
                ).show()
            }
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