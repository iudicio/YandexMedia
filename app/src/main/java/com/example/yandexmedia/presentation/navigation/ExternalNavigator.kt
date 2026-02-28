package com.example.yandexmedia.presentation.navigation

import android.app.Activity

interface ExternalNavigator {
    fun share(activity: Activity, text: String, chooserTitle: String): Boolean
    fun email(activity: Activity, to: String, subject: String, body: String): Boolean
    fun openUrl(activity: Activity, url: String): Boolean
}
