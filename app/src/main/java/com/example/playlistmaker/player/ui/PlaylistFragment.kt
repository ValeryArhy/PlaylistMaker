package com.example.playlistmaker.player.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.app.getFormattedTime
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.databinding.PlaylistItemMiniBinding
import com.example.playlistmaker.player.ui.adapter.PlaylistTracksAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var trackAdapter: PlaylistTracksAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistInfoBinding: PlaylistItemMiniBinding

    private var playlistId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getLong("playlist_id") ?: 0L
        if (playlistId == 0L) {
            Toast.makeText(requireContext(), getString(R.string.playlist_not_found), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupBottomSheets()
        setupTrackAdapter()
        setupClicks()
        setupObservers()
        setupIncludeObservers()

        viewModel.loadPlaylistTracks(playlistId)
    }

    private fun setupBottomSheets() {

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    showOverlay(newState != BottomSheetBehavior.STATE_HIDDEN)
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    binding.overlay.alpha = slideOffset
                }
            })
        }

        playlistInfoBinding =
            PlaylistItemMiniBinding.bind(binding.menuBottomSheet.findViewById(R.id.playlist_info))
    }


    private fun setupTrackAdapter() {
        trackAdapter = PlaylistTracksAdapter { track ->
            showOverlay(true)
            MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
                .setTitle(getString(R.string.delete_track_title))
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> showOverlay(false) }
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    viewModel.deleteTrack(playlistId, track.id)
                    showOverlay(false)
                }
                .setOnDismissListener { showOverlay(false) }
                .show()
        }

        binding.RecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun setupClicks() {
        binding.menuButton.setOnClickListener { findNavController().popBackStack() }

        binding.menu.setOnClickListener { toggleMenuBottomSheet() }

        binding.overlay.setOnClickListener {
            if (menuBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                closeMenuBottomSheet()
            }
        }

        binding.playlistDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
                .setTitle(getString(R.string.Deleted_playlist))
                .setMessage(getString(R.string.delete_playlist_message, viewModel.playlistName.value ?: "" ))
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    viewModel.deletePlaylist(playlistId) {
                        findNavController().popBackStack()
                    }
                }
                .show()
        }

        binding.playlistShare.setOnClickListener {
            closeMenuBottomSheet()
            sharePlaylist()
        }
        binding.shareButton.setOnClickListener {
            closeMenuBottomSheet()
            sharePlaylist()
        }
        binding.menuBottomSheet.findViewById<TextView>(R.id.edit_info).setOnClickListener {
            val id = playlistId
            if (id == 0L) return@setOnClickListener
            val action = PlaylistFragmentDirections
                .actionPlaylistFragmentToEditPlaylistFragment(id)
            findNavController().navigate(action)
        }
    }

    private fun setupObservers() {
        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.duration.text = minutes
        }
        viewModel.playlistName.observe(viewLifecycleOwner) { name ->
            binding.PlaylistName.text = name
        }
        viewModel.playlistDescription.observe(viewLifecycleOwner) { desc ->
            binding.description.text = desc ?: ""
        }
        viewModel.trackCountText.observe(viewLifecycleOwner) { text ->
            binding.trackCount.text = text
        }
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.submitList(tracks)
        }
        viewModel.isEmpty.observe(viewLifecycleOwner) { empty ->
            if (empty) Toast.makeText(requireContext(), "В плейлисте нет треков", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupIncludeObservers() {
        viewModel.playlistName.observe(viewLifecycleOwner) { name ->
            playlistInfoBinding.PlaylistName.text = name
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            playlistInfoBinding.TrackCount.text = "${tracks.size} треков"
        }

        viewModel.playlistCover.observe(viewLifecycleOwner) { coverPath ->
            val coverFile = coverPath?.let { File(it) }
            if (coverFile != null && coverFile.exists()) {
                Glide.with(binding.placeholderImage.context)
                    .load(coverFile)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(binding.placeholderImage)
            }
        }
    }

    private fun toggleMenuBottomSheet() {
        when (menuBottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_HIDDEN -> {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                showOverlay(true)
            }
            else -> closeMenuBottomSheet()
        }
    }

    private fun closeMenuBottomSheet() {
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        showOverlay(false)
    }

    private fun showOverlay(visible: Boolean) {
        binding.overlay.isVisible = visible
        if (visible) binding.overlay.alpha = 1f else binding.overlay.alpha = 0f
    }

    private fun sharePlaylist() {
        val tracks = viewModel.tracks.value
        if (tracks.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val playlistName = viewModel.playlistName.value ?: ""
        val description = viewModel.playlistDescription.value ?: ""
        val trackCountText = viewModel.trackCountText.value ?: ""

        val trackListText = tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artist} - ${track.name} (${track.getFormattedTime()})"
        }.joinToString("\n")

        val shareText = "$playlistName\n$description\n$trackCountText\n$trackListText"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, ""))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
