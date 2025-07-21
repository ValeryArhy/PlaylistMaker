package com.example.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.presentation.SearchItem
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.app.dpToPx
import com.example.playlistmaker.app.getFormattedTime


class TrackAdapter(
    private val onTrackClick: (Track) -> Unit = {}
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private val items = mutableListOf<SearchItem.TrackItem>()

    fun submitList(newItems: List<SearchItem.TrackItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class TrackViewHolder(
        itemView: View,
        private val onTrackClick: (Track) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
        private val artwork: ImageView = itemView.findViewById(R.id.icon_album)

        fun bind(item: SearchItem.TrackItem) {
            val track = item.track
            trackName.text = track.name
            artistName.text = track.artist
            trackTime.text = track.getFormattedTime()

            Glide.with(itemView.context)
                .load(track.artworkUrl)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(itemView.context.dpToPx(2)))
                .into(artwork)

            itemView.setOnClickListener {
                onTrackClick(track)
            }
        }
    }
}