package com.example.yandexmedia.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.theme.YandexDisplay

@Composable
fun TrackRow(track: Track, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val textColor = colorResource(R.color.color_black)
    val secondaryColor = colorResource(R.color.color_track_gray)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = stringResource(R.string.content_album_art),
            placeholder = painterResource(R.drawable.ic_placeholder),
            error = painterResource(R.drawable.ic_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
        )
        Column(
            Modifier
                .weight(1f)
                .padding(start = 13.dp)
        ) {
            Text(
                text = track.trackName,
                color = textColor,
                fontFamily = YandexDisplay,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    color = secondaryColor,
                    fontFamily = YandexDisplay,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    painter = painterResource(R.drawable.ellipse_1),
                    contentDescription = null,
                    tint = secondaryColor,
                    modifier = Modifier.padding(horizontal = 4.dp).size(3.dp)
                )
                Text(
                    text = track.trackTime,
                    color = secondaryColor,
                    fontFamily = YandexDisplay,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = secondaryColor,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
        )
    }
}

@Composable
fun PlaylistCard(playlist: Playlist, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val textColor = colorResource(R.color.color_white_text)
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = playlist.coverPath,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_placeholder),
            error = painterResource(R.drawable.ic_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
        )
        Text(
            text = playlist.name,
            color = textColor,
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 1.dp)
        )
        Text(
            text = stringResource(R.string.playlist_tracks_count, playlist.tracksCount),
            color = textColor,
            fontFamily = YandexDisplay,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
