package com.example.playlistmaker.player.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : NewPlaylistFragment() {

    private val playlistId by lazy {
        arguments?.getLong("playlist_id") ?: 0L
    }

    override val viewModel: EditPlaylistViewModel by viewModel { parametersOf(playlistId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.title = getString(R.string.edit_playlist_title)
        binding.savePlaylist.text = getString(R.string.save)


        viewModel.loadPlaylist()


        viewModel.initialPlaylistName.observe(viewLifecycleOwner) { binding.addName.setText(it) }
        viewModel.initialDescription.observe(viewLifecycleOwner) { binding.description.setText(it) }
        viewModel.initialCoverPath.observe(viewLifecycleOwner) { path ->
            path?.let { loadImage(Uri.fromFile(File(it))) }
        }


        binding.savePlaylist.setOnClickListener {
            val name = binding.addName.text.toString().trim()
            if (name.isEmpty()) {
                return@setOnClickListener
            }

            val description = binding.description.text.toString().trim()
            val currentCover = coverPath ?: viewModel.initialCoverPath.value

            viewModel.saveChanges(name, description, currentCover) {
                Toast.makeText(requireContext(), getString(R.string.update_playlist), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }


        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }
}
