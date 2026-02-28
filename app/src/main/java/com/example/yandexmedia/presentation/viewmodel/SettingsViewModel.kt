package com.example.yandexmedia.presentation.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.example.yandexmedia.domain.interactor.ThemeInteractor
import com.example.yandexmedia.presentation.navigation.ExternalNavigator

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val externalNavigator: ExternalNavigator
) : ViewModel() {

    fun isDarkTheme(): Boolean = themeInteractor.isDarkTheme()

    fun onThemeChanged(enabled: Boolean) {
        themeInteractor.setDarkTheme(enabled)
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun share(activity: android.app.Activity, text: String, chooserTitle: String): Boolean =
        externalNavigator.share(activity, text, chooserTitle)

    fun email(activity: android.app.Activity, to: String, subject: String, body: String): Boolean =
        externalNavigator.email(activity, to, subject, body)

    fun openUrl(activity: android.app.Activity, url: String): Boolean =
        externalNavigator.openUrl(activity, url)
}