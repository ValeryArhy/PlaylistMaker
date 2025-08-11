package com.example.playlistmaker.player.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.dpToPx
import com.example.playlistmaker.app.getCoverArtwork
import com.example.playlistmaker.app.getFormattedTime
import com.example.playlistmaker.app.getYearFromReleaseDate
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.player.domain.api.GetLastTrackUseCase
import com.example.playlistmaker.player.domain.api.SaveLastTrackUseCase
import com.example.playlistmaker.search.domain.model.Track
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding

    private val getLastTrackUseCase: GetLastTrackUseCase by inject()
    private val saveLastTrackUseCase: SaveLastTrackUseCase by inject()
    private val viewModel: MediaViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        viewModel.isPlaying.observe(this, Observer { isPlaying ->
            binding.play.isSelected = isPlaying
        })
        viewModel.trackPosition.observe(this, Observer { time ->
            binding.trackTime.text = time
        })
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