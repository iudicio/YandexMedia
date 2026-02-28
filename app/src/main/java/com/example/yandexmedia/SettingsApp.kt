package com.example.yandexmedia

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.yandexmedia.di.appModule
import com.example.yandexmedia.domain.interactor.ThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

class SettingsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SettingsApp)
            modules(appModule)
        }

        applyThemeFromPrefs()
    }

    private fun applyThemeFromPrefs() {
        val themeInteractor = getKoin().get<ThemeInteractor>()
        AppCompatDelegate.setDefaultNightMode(
            if (themeInteractor.isDarkTheme())
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}