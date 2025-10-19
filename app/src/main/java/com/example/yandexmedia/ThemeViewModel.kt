package com.example.yandexmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmedia.data.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = ThemePreferences(application)

    val isDarkMode = prefs.isDarkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            prefs.setDarkMode(isDark)
        }
    }
}
