package com.example.playlistmaker.player.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.app.getFormattedTime
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.search.domain.model.Track



class PlaylistTracksAdapter(
    private val onRemoveClick: (Track) -> Unit
) : RecyclerView.Adapter<PlaylistTracksAdapter.TrackViewHolder>() {

    private val tracks = mutableListOf<Track>()

    fun submitList(newList: List<Track>) {
        tracks.clear()
        tracks.addAll(newList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    inner class TrackViewHolder(private val binding: TrackItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.trackName.text = track.name
            binding.artistName.text = track.artist
            binding.trackTime.text = track.getFormattedTime()

            Glide.with(binding.iconAlbum.context)
                .load(track.artworkUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.iconAlbum)

            itemView.setOnLongClickListener {
                onRemoveClick(track)
                true
            }
        }
    }
}