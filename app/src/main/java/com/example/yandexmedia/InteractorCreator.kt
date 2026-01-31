package com.example.yandexmedia.creator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.example.yandexmedia.presentation.navigation.ExternalNavigator
import com.example.yandexmedia.presentation.navigation.ExternalNavigatorImpl
import com.example.yandexmedia.presentation.viewmodel.SearchViewModel

object InteractorCreator {

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val repository = ThemeRepository(prefs)
        return ThemeInteractorImpl(repository)
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val prefs = context.getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(prefs)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(
            repository = provideSearchHistoryRepository(context)
        )
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(
            repository = SearchRepositoryImpl()
        )
    }

    fun provideExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl()
    }

    fun provideSearchViewModelFactory(): ViewModelProvider.Factory {
        val interactor = provideSearchInteractor()

        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                    return SearchViewModel(interactor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
            }
        }
    }
}
