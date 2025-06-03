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

    var tracks = ArrayList<Track>()
    private var listener: ((Track) -> Unit)? = null

    fun setTracks(tracks: List<Track>) {
        this.tracks.clear()
        this.tracks.addAll(tracks)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Track) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            listener?.invoke(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    class TrackViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        private val trackName: TextView = parentView.findViewById(R.id.trackName)
        private val artistName: TextView = parentView.findViewById(R.id.artistName)
        private val trackTimeMillis: TextView = parentView.findViewById(R.id.trackTime)
        private val artworkUrl100: ImageView = parentView.findViewById(R.id.icon_album)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTimeMillis.text = track.getFormattedTime()

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(4))
                .into(artworkUrl100)

        }
    }
}

