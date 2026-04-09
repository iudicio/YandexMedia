package com.example.yandexmedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.SearchHistoryInteractor
import com.example.yandexmedia.domain.interactor.SearchInteractor
import com.example.yandexmedia.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    private val _history = MutableStateFlow<List<Track>>(emptyList())
    val history: StateFlow<List<Track>> = _history

    private var lastQuery: String = ""
    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
    }

    fun loadHistory() {
        _history.value = historyInteractor.getHistory()
    }

    fun clearHistory() {
        historyInteractor.clear()
        _history.value = emptyList()
    }

    fun addToHistory(track: Track) {
        historyInteractor.addTrack(track)
        _history.value = historyInteractor.getHistory()
    }

    fun onQueryChanged(text: String) {
        lastQuery = text
        searchJob?.cancel()

        if (text.length <= 2) {
            _state.value = SearchState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)

            if (text != lastQuery || text.length <= 2) return@launch

            _state.value = SearchState.Loading

            searchInteractor.search(text).collect { result ->
                when (result) {
                    is SearchInteractor.SearchResult.Success -> {
                        _state.value = if (result.tracks.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.Content(result.tracks)
                        }
                    }

                    SearchInteractor.SearchResult.NetworkError -> {
                        _state.value = SearchState.NetworkError
                    }

                    is SearchInteractor.SearchResult.Error -> {
                        _state.value = SearchState.NetworkError
                    }
                }
            }
        }
    }

    fun onRetry() {
        onQueryChanged(lastQuery)
    }
}