package com.example.yandexmedia.presentation.ui.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.domain.interactor.FavoritesInteractor
import com.example.yandexmedia.presentation.ui.media.model.FavoritesTracksState
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesTracksState>()
    val state: LiveData<FavoritesTracksState> = _state

    init {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { tracks ->
                _state.postValue(
                    if (tracks.isEmpty()) FavoritesTracksState.Empty
                    else FavoritesTracksState.Content(tracks)
                )
            }
        }
    }
}