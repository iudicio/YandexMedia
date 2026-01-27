package com.example.yandexmedia.creator

import android.content.Context
import com.example.yandexmedia.data.domain.SearchHistory
import com.example.yandexmedia.data.interactor.SearchHistoryInteractorImpl
import com.example.yandexmedia.data.interactor.SearchInteractorImpl
import com.example.yandexmedia.data.interactor.ThemeInteractorImpl
import com.example.yandexmedia.data.repository.SearchRepository
import com.example.yandexmedia.data.repository.ThemeRepository
import com.example.yandexmedia.domain.interactor.SearchHistoryInteractor
import com.example.yandexmedia.domain.interactor.SearchInteractor
import com.example.yandexmedia.domain.interactor.ThemeInteractor

object InteractorCreator {

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val repository = ThemeRepository(prefs)
        return ThemeInteractorImpl(repository)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val prefs = context.getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)
        val storage = SearchHistory(prefs)
        return SearchHistoryInteractorImpl(storage)
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(SearchRepository())
    }
}
