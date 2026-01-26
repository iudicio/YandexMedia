package com.example.yandexmedia.presentation

import android.content.Context
import com.example.yandexmedia.data.repository.SearchHistoryRepositoryImpl
import com.example.yandexmedia.domain.interactor.AddTrackToHistoryInteractor
import com.example.yandexmedia.domain.interactor.GetSearchHistoryInteractor
import com.example.yandexmedia.domain.repository.SearchHistoryRepository

object Creator {

    private fun provideSearchHistoryRepository(
        context: Context
    ): SearchHistoryRepository {

        val prefs = context.getSharedPreferences(
            "search_history",
            Context.MODE_PRIVATE
        )

        return SearchHistoryRepositoryImpl(prefs)
    }

    fun provideGetSearchHistoryInteractor(
        context: Context
    ): GetSearchHistoryInteractor {

        return GetSearchHistoryInteractor(
            provideSearchHistoryRepository(context)
        )
    }

    fun provideAddTrackToHistoryInteractor(
        context: Context
    ): AddTrackToHistoryInteractor {

        return AddTrackToHistoryInteractor(
            provideSearchHistoryRepository(context)
        )
    }
}
