package com.example.yandexmedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.SearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    private var lastQuery: String = ""
    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
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

            when (val result = searchInteractor.search(text)) {
                is SearchInteractor.SearchResult.Success -> {
                    val tracks = result.tracks
                    _state.value = if (tracks.isEmpty()) SearchState.Empty else SearchState.Content(tracks)
                }
                SearchInteractor.SearchResult.NetworkError -> _state.value = SearchState.NetworkError
                is SearchInteractor.SearchResult.Error -> _state.value = SearchState.NetworkError
            }
        }
    }

    fun onRetry() {
        onQueryChanged(lastQuery)
    }
}
