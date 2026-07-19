package com.example.yandexmedia.presentation.ui.media

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.theme.YandexDisplay
import com.example.yandexmedia.presentation.ui.components.PlaylistCard
import com.example.yandexmedia.presentation.ui.components.TrackRow
import com.example.yandexmedia.presentation.ui.media.model.FavoritesTracksState
import com.example.yandexmedia.presentation.ui.media.model.PlaylistsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryScreen(
    favoritesState: FavoritesTracksState,
    playlistsState: PlaylistsState,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylist: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val favoritesListState = rememberLazyListState()
    val playlistsGridState = rememberLazyGridState()
    var tabTextWidths by remember { mutableStateOf(listOf(0.dp, 0.dp)) }
    val background = colorResource(R.color.color_back_ground)
    val textColor = colorResource(R.color.color_black)
    Column(
        Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Text(
            text = stringResource(R.string.title_media),
            color = textColor,
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)
        )
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = colorResource(R.color.color_primary_white),
            contentColor = textColor,
            modifier = Modifier.padding(top = 12.dp),
            indicator = { positions ->
                val tabPosition = positions[pagerState.currentPage]
                val measuredWidth = tabTextWidths[pagerState.currentPage]
                val indicatorWidth = if (measuredWidth > 0.dp) measuredWidth else tabPosition.width
                val sidePadding = ((tabPosition.width - indicatorWidth) / 2).coerceAtLeast(0.dp)
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPosition)
                        .padding(horizontal = sidePadding),
                    color = textColor
                )
            },
            divider = {}
        ) {
            LibraryTab(
                selected = pagerState.currentPage == 0,
                text = stringResource(R.string.tab_favorites),
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                onTextWidthChanged = { width ->
                    if (tabTextWidths[0] != width) tabTextWidths = listOf(width, tabTextWidths[1])
                }
            )
            LibraryTab(
                selected = pagerState.currentPage == 1,
                text = stringResource(R.string.tab_playlists),
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                onTextWidthChanged = { width ->
                    if (tabTextWidths[1] != width) tabTextWidths = listOf(tabTextWidths[0], width)
                }
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            if (page == 0) {
                FavoritesContent(favoritesState, favoritesListState, onTrackClick)
            } else {
                PlaylistsContent(playlistsState, playlistsGridState, onPlaylistClick, onNewPlaylist)
            }
        }
    }
}

@Composable
private fun LibraryTab(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    onTextWidthChanged: (Dp) -> Unit
) {
    val density = LocalDensity.current
    Tab(
        selected = selected,
        onClick = onClick,
        selectedContentColor = colorResource(R.color.color_white_text),
        unselectedContentColor = colorResource(R.color.color_track_gray),
        text = {
            Text(
                text = text,
                fontFamily = YandexDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                onTextLayout = { result ->
                    onTextWidthChanged(with(density) { result.size.width.toDp() })
                }
            )
        }
    )
}

@Composable
private fun FavoritesContent(
    state: FavoritesTracksState,
    listState: LazyListState,
    onClick: (Track) -> Unit
) {
    when (state) {
        FavoritesTracksState.Empty -> EmptyContent(
            message = stringResource(R.string.empty_library_message),
            topPadding = 100.dp
        )
        is FavoritesTracksState.Content -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(state.tracks, key = { it.trackId }) { track ->
                TrackRow(track = track, onClick = { onClick(track) })
            }
        }
    }
}

@Composable
private fun PlaylistsContent(
    state: PlaylistsState,
    gridState: LazyGridState,
    onClick: (Playlist) -> Unit,
    onNew: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .width(300.dp)
                .height(48.dp)
                .clickable(onClick = onNew),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.new_playlist),
                contentDescription = stringResource(R.string.new_playlist),
                modifier = Modifier.width(133.dp).height(36.dp)
            )
        }
        when (state) {
            PlaylistsState.Empty -> EmptyContent(
                message = stringResource(R.string.empty_playlists_message),
                topPadding = 46.dp
            )
            is PlaylistsState.Content -> {
                Spacer(Modifier.height(24.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    state = gridState,
                    contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp)
                ) {
                    items(state.playlists, key = { it.id }) { playlist ->
                        PlaylistCard(playlist = playlist, onClick = { onClick(playlist) })
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(message: String, topPadding: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_logo_media),
            contentDescription = message,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = message,
            color = colorResource(R.color.color_white_text),
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
