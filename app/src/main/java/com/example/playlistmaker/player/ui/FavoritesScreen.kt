package com.example.playlistmaker.media.ui



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.FavoritesViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.Placeholder
import com.example.playlistmaker.search.ui.TrackList

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onTrackClick: (Track) -> Unit
) {
    val tracks by viewModel.favoriteTracks.collectAsState()

    if (tracks.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 102.dp)
        ) {
            Placeholder(
                imageRes = R.drawable.search_null,
                message = stringResource(R.string.empty_list_favor)
            )
        }
    } else {
        TrackList(
            tracks = tracks,
            onTrackClick = onTrackClick
        )
    }
}