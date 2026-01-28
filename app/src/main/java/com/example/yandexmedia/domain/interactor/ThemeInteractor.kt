package com.example.yandexmedia.domain.interactor

interface ThemeInteractor {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}
