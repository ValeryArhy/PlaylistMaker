package com.example.playlistmaker.player.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.player.domain.model.Playlist
import java.io.File

class PlaylistsAdapter(
    private val onItemClick: (playlistId: Long) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun setItems(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount() = playlists.size

    inner class PlaylistViewHolder(private val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.PlaylistName.text = playlist.name
            binding.TrackCount.text = "${playlist.trackCount} треков"

            val coverFile = playlist.coverPath?.let { File(it) }

            Glide.with(binding.PlaylistCover.context)
                .load(coverFile)
                .into(binding.PlaylistCover)
            binding.root.setOnClickListener {
                onItemClick(playlist.id)
            }

        }
    }
}