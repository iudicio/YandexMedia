package com.example.yandexmedia   // ОБЯЗАТЕЛЬНО этот же пакет

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class SettingsApp : Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        darkTheme = prefs.getBoolean("dark_theme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        prefs.edit()
            .putBoolean("dark_theme", darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
