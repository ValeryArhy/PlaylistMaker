package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class MediaActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private lateinit var play: ImageButton
    private lateinit var trackTimeView: TextView

    private var mediaPlayer = MediaPlayer()
    private var url: String? = null
    private var isTrackCompleted = false
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                trackTimeView.text = getFormattedTime(mediaPlayer.currentPosition)
                handler.postDelayed(this, 200)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        val exitButton = findViewById<ImageButton>(R.id.menu_button)
        val newPlaylist = findViewById<ImageButton>(R.id.new_playlist)
        val favorite = findViewById<ImageButton>(R.id.favorites)
        play = findViewById(R.id.play)
        trackTimeView = findViewById((R.id.trackTime))
        val track = intent.getSerializableExtra("track") as? Track
        if (track == null) {
            finish()
            return
        }

        url = track.previewUrl

        preparePlayer()

        mediaPlayer.setOnCompletionListener {
            isTrackCompleted = true
            playerState = STATE_PAUSED
            play.isSelected = false
            handler.removeCallbacks(updateTimerRunnable)
            trackTimeView.text = getFormattedTime(0)
        }

        play.setOnClickListener {
            playbackControl()
        }

        exitButton.setOnClickListener { finish() }

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
            .transform(RoundedCorners(this.dpToPx(8)))
            .into(artworkUrl100)
    }


    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isSelected = false
            playerState = STATE_PREPARED
            trackTimeView.text = getFormattedTime(0)
        }
    }

    private fun startPlayer() {
        if (isTrackCompleted) {
            mediaPlayer.seekTo(0)
            isTrackCompleted = false
        }
        mediaPlayer.start()
        play.isSelected = true
        playerState = STATE_PLAYING
        handler.post(updateTimerRunnable)
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        play.isSelected = false
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimerRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun getFormattedTime(milliseconds: Int): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(milliseconds)
    }
}


