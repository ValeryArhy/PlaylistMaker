package com.example.playlistmaker.player.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemMiniBinding
import com.example.playlistmaker.player.domain.model.Playlist
import java.io.File

class PlaylistAdapterMini(
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapterMini.PlaylistMiniViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setItems(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistMiniViewHolder {
        val binding = PlaylistItemMiniBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistMiniViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistMiniViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistMiniViewHolder(
        private val binding: PlaylistItemMiniBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.PlaylistName.text = playlist.name
            binding.TrackCount.text = "${playlist.trackCount} треков"

            val coverFile = playlist.coverPath?.let { File(it) }

            if (coverFile != null && coverFile.exists()) {
                Glide.with(binding.PlaylistCover.context)
                    .load(coverFile)
                    .into(binding.PlaylistCover)
            } else {
                binding.PlaylistCover.setImageResource(R.drawable.placeholder2)
            }
            binding.root.setOnClickListener {
                onPlaylistClick.invoke(playlist)
            }
        }
    }
}
