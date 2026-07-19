package com.example.yandexmedia.presentation.ui.media

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.theme.YandexMediaTheme
import com.example.yandexmedia.presentation.ui.media.model.FavoritesTracksState
import com.example.yandexmedia.presentation.ui.media.viewmodel.FavoritesTracksViewModel
import com.example.yandexmedia.presentation.ui.media.viewmodel.PlaylistsViewModel
import com.example.yandexmedia.presentation.ui.media.model.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment() {
    private val favoritesViewModel: FavoritesTracksViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()
    private var lastNavigationAt = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        ComposeView(requireContext()).apply {
            id = R.id.compose_view_media_library
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val favorites by favoritesViewModel.state.observeAsState(FavoritesTracksState.Empty)
                val playlists by playlistsViewModel.state.observeAsState(PlaylistsState.Empty)
                YandexMediaTheme {
                    MediaLibraryScreen(
                        favoritesState = favorites,
                        playlistsState = playlists,
                        onTrackClick = { navigateFromMedia(R.id.action_mediaLibraryFragment_to_playerFragment, bundleOf("track" to it)) },
                        onPlaylistClick = { navigateFromMedia(R.id.playlistFragment, bundleOf("playlistId" to it.id)) },
                        onNewPlaylist = { navigateFromMedia(R.id.action_mediaLibraryFragment_to_createPlaylistFragment) }
                    )
                }
            }
        }

    private fun navigateFromMedia(action: Int, args: Bundle? = null) {
        val navController = findNavController()
        val now = SystemClock.elapsedRealtime()
        if (navController.currentDestination?.id != R.id.mediaLibraryFragment ||
            now - lastNavigationAt < NAVIGATION_DEBOUNCE_DELAY
        ) return
        lastNavigationAt = now
        navController.navigate(action, args)
    }

    private companion object {
        const val NAVIGATION_DEBOUNCE_DELAY = 500L
    }
}
