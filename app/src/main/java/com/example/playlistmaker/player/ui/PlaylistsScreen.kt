package com.example.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.model.Playlist
import com.example.playlistmaker.player.ui.PlaylistsViewModel
import java.io.File

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onPlaylistClick: (Long) -> Unit,
    onNewPlaylistClick: () -> Unit
) {
    val playlists by viewModel.playlists.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = onNewPlaylistClick,
            modifier = Modifier.padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                color = MaterialTheme.colorScheme.surface
            )
        }

        if (playlists.isEmpty()) {

            PlaylistPlaceholder(modifier = Modifier.padding(top = 46.dp))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(playlists) { playlist ->
                    PlaylistItem(playlist) { onPlaylistClick(playlist.id) }
                }
            }
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            factory = { context ->
                android.widget.ImageView(context).apply {
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                }
            },
            update = { imageView ->
                Glide.with(imageView.context)
                    .load(playlist.coverPath?.let { File(it) })
                    .placeholder(R.drawable.placeholder2)
                    .error(R.drawable.placeholder2)
                    .into(imageView)
            }
        )

        Text(
            text = playlist.name,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = getTrackCountString(playlist.trackCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlaylistPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.search_null),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = stringResource(R.string.no_playlist),
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
@Composable
fun getTrackCountString(count: Int): String {
    val lastDigit = count % 10
    val lastTwoDigits = count % 100

    return when {
        lastTwoDigits in 11..19 -> "$count треков"
        lastDigit == 1 -> "$count трек"
        lastDigit in 2..4 -> "$count трека"
        else -> "$count треков"
    }
}