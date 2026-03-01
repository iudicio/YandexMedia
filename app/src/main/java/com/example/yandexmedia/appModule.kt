package com.example.yandexmedia.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.yandexmedia.data.interactor.ThemeInteractorImpl
import com.example.yandexmedia.data.repository.SearchHistoryRepositoryImpl
import com.example.yandexmedia.data.repository.SearchRepositoryImpl
import com.example.yandexmedia.data.repository.ThemeRepository
import com.example.yandexmedia.domain.interactor.SearchHistoryInteractor
import com.example.yandexmedia.domain.interactor.SearchHistoryInteractorImpl
import com.example.yandexmedia.domain.interactor.SearchInteractor
import com.example.yandexmedia.domain.interactor.SearchInteractorImpl
import com.example.yandexmedia.domain.interactor.ThemeInteractor
import com.example.yandexmedia.domain.repository.SearchHistoryRepository
import com.example.yandexmedia.domain.repository.SearchRepository
import com.example.yandexmedia.presentation.navigation.ExternalNavigator
import com.example.yandexmedia.presentation.navigation.ExternalNavigatorImpl
import com.example.yandexmedia.presentation.viewmodel.PlayerViewModel
import com.example.yandexmedia.presentation.viewmodel.SearchViewModel
import com.example.yandexmedia.presentation.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

interface MediaPlayerProvider {
    fun create(): android.media.MediaPlayer
}

class AndroidMediaPlayerProvider : MediaPlayerProvider {
    override fun create(): android.media.MediaPlayer = android.media.MediaPlayer()
}

val appModule = module {

    single(named("app_settings_prefs")) {
        androidContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    }
    single(named("search_history_prefs")) {
        androidContext().getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)
    }

    single { ThemeRepository(get(named("app_settings_prefs"))) }

    single<SearchRepository> { SearchRepositoryImpl() }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(named("search_history_prefs"))) }

    single<ThemeInteractor> { ThemeInteractorImpl(get()) }
    single<SearchInteractor> { SearchInteractorImpl(get()) }
    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }

    single<ExternalNavigator> { ExternalNavigatorImpl() }

    single { Handler(Looper.getMainLooper()) }
    single<MediaPlayerProvider> { AndroidMediaPlayerProvider() }

    viewModel { SearchViewModel(searchInteractor = get(), historyInteractor = get()) }
    viewModel { PlayerViewModel(handler = get(), mediaPlayerProvider = get()) }
    viewModel { SettingsViewModel(themeInteractor = get(), externalNavigator = get()) }
}