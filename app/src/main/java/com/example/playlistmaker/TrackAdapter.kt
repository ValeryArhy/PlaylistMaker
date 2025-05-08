package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    var trackList = ArrayList<Track>()

    fun setTracks(tracks: List<Track>) {
        trackList.clear()
        trackList.addAll(tracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    class TrackViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        private val trackName: TextView = parentView.findViewById(R.id.trackName)
        private val artistName: TextView = parentView.findViewById(R.id.artistName)
        private val trackTimeMillis: TextView = parentView.findViewById(R.id.trackTime)
        private val artworkUrl100: ImageView = parentView.findViewById(R.id.icon_album)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTimeMillis.text = formatTime(track.trackTimeMillis)


            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(4))
                .into(artworkUrl100)
        }
        private fun formatTime(millis: Long): String {
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            return dateFormat.format(millis)
        }

    }
}

