package com.example.yandexmedia.presentation.ui.search

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.theme.YandexMediaTheme
import com.example.yandexmedia.presentation.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModel()
    private var lastTrackClickAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.query.value.isEmpty()) {
            savedInstanceState?.getString(STATE_QUERY)?.let(viewModel::onQueryChanged)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_QUERY, viewModel.query.value)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        ComposeView(requireContext()).apply {
            id = R.id.compose_view_search
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val searchState by viewModel.state.collectAsState()
                val history by viewModel.history.collectAsState()
                val query by viewModel.query.collectAsState()
                YandexMediaTheme {
                    SearchScreen(
                        query = query,
                        state = searchState,
                        history = history,
                        onQueryChange = viewModel::onQueryChanged,
                        onTrackClick = { openPlayer(it, addToHistory = true) },
                        onHistoryTrackClick = { openPlayer(it, addToHistory = false) },
                        onClearHistory = viewModel::clearHistory,
                        onRetry = viewModel::onRetry
                    )
                }
            }
        }

    override fun onResume() {
        super.onResume()
        viewModel.loadHistory()
    }

    private fun openPlayer(track: Track, addToHistory: Boolean) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastTrackClickAt < CLICK_DEBOUNCE_DELAY) return
        if (findNavController().currentDestination?.id == R.id.searchFragment) {
            lastTrackClickAt = now
            if (addToHistory) viewModel.addToHistory(track)
            findNavController().navigate(R.id.playerFragment, bundleOf("track" to track))
        }
    }

    private companion object {
        const val CLICK_DEBOUNCE_DELAY = 1_000L
        const val STATE_QUERY = "search_query"
    }
}
