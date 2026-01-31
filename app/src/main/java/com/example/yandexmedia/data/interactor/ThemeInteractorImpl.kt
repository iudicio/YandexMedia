package com.example.yandexmedia.data.interactor

import com.example.yandexmedia.data.repository.ThemeRepository
import com.example.yandexmedia.domain.interactor.ThemeInteractor

class ThemeInteractorImpl(
    private val repository: ThemeRepository
) : ThemeInteractor {

    override fun isDarkTheme(): Boolean = repository.isDarkTheme()

    override fun setDarkTheme(enabled: Boolean) {
        repository.setDarkTheme(enabled)
    }
}
