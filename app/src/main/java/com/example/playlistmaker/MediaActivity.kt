package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        val exitButton = findViewById<ImageButton>(R.id.menu_button)
        val newPlaylist = findViewById<ImageButton>(R.id.new_playlist)
        val play = findViewById<ImageButton>(R.id.play)
        val favorite = findViewById<ImageButton>(R.id.favorites)

        exitButton.setOnClickListener { finish() }

        val track = intent.getSerializableExtra("track") as? Track
        if (track != null) {
            val trackName = findViewById<TextView>(R.id.trackName)
            val artistName = findViewById<TextView>(R.id.artistName)
            val collection = findViewById<TextView>(R.id.album_adapt)
            val releaseDate = findViewById<TextView>(R.id.years_adapt)
            val genreView = findViewById<TextView>(R.id.genre_adapt)
            val countryView = findViewById<TextView>(R.id.country_adapt)
            val trackTimeMillis = findViewById<TextView>(R.id.duration_adapt)
            val artworkUrl100 = findViewById<ImageView>(R.id.icon_album)

            trackName.text = track.trackName
            artistName.text = track.artistName
            collection.text = track.collectionName
            releaseDate.text = track.getYearFromReleaseDate()
            genreView.text = track.primaryGenreName
            countryView.text = track.country
            trackTimeMillis.text = track.getFormattedTime()


            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.placeholder2)
                .transform(RoundedCorners(30))
                .into(artworkUrl100)

        } else {
            finish()
        }

    }
}