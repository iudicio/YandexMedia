package com.example.yandexmedia

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.yandexmedia.creator.InteractorCreator
import com.example.yandexmedia.domain.interactor.ThemeInteractor

class SettingsApp : Application() {

    lateinit var themeInteractor: ThemeInteractor
        private set

    override fun onCreate() {
        super.onCreate()

        themeInteractor = InteractorCreator.provideThemeInteractor(this)
        applyTheme()
    }

    fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (themeInteractor.isDarkTheme())
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
