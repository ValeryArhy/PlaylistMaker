package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.FavoritesScreen
import com.example.playlistmaker.media.ui.MediaLibraryScreen
import com.example.playlistmaker.media.ui.PlaylistsScreen
import com.example.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme {
                    MediaLibraryScreen(
                        favoritesContent = {
                            FavoritesScreen(
                                viewModel = favoritesViewModel,
                                onTrackClick = { track ->
                                    findNavController().navigate(
                                        R.id.playerFragment,
                                        bundleOf("track" to track)
                                    )
                                }
                            )
                        },
                        playlistsContent = {
                            PlaylistsScreen(
                                viewModel = playlistsViewModel,
                                onPlaylistClick = { id ->
                                    val bundle = bundleOf("playlist_id" to id)
                                    findNavController().navigate(
                                        R.id.action_mediaLibraryFragment_to_playlistFragment,
                                        bundle
                                    )
                                },
                                onNewPlaylistClick = {
                                    findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playlistsViewModel.loadPlaylists()
    }
}