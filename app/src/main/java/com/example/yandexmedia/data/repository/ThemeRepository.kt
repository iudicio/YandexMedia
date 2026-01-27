package com.example.yandexmedia.data.repository

import android.content.SharedPreferences

class ThemeRepository(private val prefs: SharedPreferences) {

    fun isDarkTheme(): Boolean =
        prefs.getBoolean(KEY_DARK_THEME, false)

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }

    private companion object {
        const val KEY_DARK_THEME = "dark_theme"
    }
}
