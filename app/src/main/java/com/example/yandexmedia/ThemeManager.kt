package com.example.yandexmedia

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_DARK_THEME = "dark_theme"

    /** Применяет сохранённую тему при старте экрана */
    fun applySavedTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean(KEY_DARK_THEME, false)
        setDarkMode(isDark)
    }

    /** Включает/выключает тёмную тему и сохраняет выбор */
    fun toggleTheme(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
        setDarkMode(isDark)
    }

    private fun setDarkMode(isDark: Boolean) {
        val mode = if (isDark)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
