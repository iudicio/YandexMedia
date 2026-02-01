package com.example.yandexmedia.presentation.viewmodel

sealed interface PlayerState {
    data object Idle : PlayerState
    data object Prepared : PlayerState
    data class Playing(val position: String) : PlayerState
    data class Paused(val position: String) : PlayerState
    data object Completed : PlayerState
    data class Error(val throwable: Throwable? = null) : PlayerState
}
