package com.example.playlistmaker.ui.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.AudioPlayerImpl
import com.example.playlistmaker.data.TrackHistoryRepositoryImpl
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.use_case.GetLastTrackUseCase
import com.example.playlistmaker.domain.use_case.SaveLastTrackUseCase
import com.example.playlistmaker.domain.use_case.TrackPlayerUseCase
import com.example.playlistmaker.dpToPx
import com.example.playlistmaker.getCoverArtwork
import com.example.playlistmaker.getFormattedTime
import com.example.playlistmaker.getYearFromReleaseDate
import com.example.playlistmaker.presentation.MediaViewModel
import com.example.playlistmaker.presentation.MediaViewModelFactory


class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var viewModel: MediaViewModel

    private lateinit var getLastTrackUseCase: GetLastTrackUseCase
    private lateinit var saveLastTrackUseCase: SaveLastTrackUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val trackHistoryRepository = TrackHistoryRepositoryImpl(sharedPrefs)

        getLastTrackUseCase = GetLastTrackUseCase(trackHistoryRepository)
        saveLastTrackUseCase = SaveLastTrackUseCase(trackHistoryRepository)

        val audioPlayer = AudioPlayerImpl()
        val playerUseCase = TrackPlayerUseCase(audioPlayer)
        viewModel = ViewModelProvider(this, MediaViewModelFactory(playerUseCase))[MediaViewModel::class.java]

        val trackFromIntent = intent.getSerializableExtra("track") as? Track
        val track = trackFromIntent ?: getLastTrackUseCase.execute()

        if (track == null) {
            finish()
            return
        }

        setupUI(track)
        observeViewModel()
        viewModel.prepare(track.previewUrl ?: "", {}, {})

        saveLastTrackUseCase.execute(track)

        binding.play.setOnClickListener { viewModel.togglePlayPause() }
        binding.menuButton.setOnClickListener { finish() }
    }

    private fun setupUI(track: Track) {
        binding.trackName.text = track.name
        binding.artistName.text = track.artist
        binding.albumAdapt.text = track.album
        binding.yearsAdapt.text = track.getYearFromReleaseDate()
        binding.genreAdapt.text = track.genre
        binding.countryAdapt.text = track.country
        binding.durationAdapt.text = track.getFormattedTime()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(dpToPx(8)))
            .into(binding.iconAlbum)
    }

    private fun observeViewModel() {
        viewModel.isPlaying.observe(this) { isPlaying ->
            binding.play.isSelected = isPlaying
        }
        viewModel.trackPosition.observe(this) { time ->
            binding.trackTime.text = time
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }
}