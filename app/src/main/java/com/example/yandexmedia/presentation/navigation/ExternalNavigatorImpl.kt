package com.example.yandexmedia.presentation.navigation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

class ExternalNavigatorImpl : ExternalNavigator {

    override fun share(activity: Activity, text: String, chooserTitle: String): Boolean {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        return try {
            activity.startActivity(Intent.createChooser(intent, chooserTitle))
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    override fun email(activity: Activity, to: String, subject: String, body: String): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        return try {
            activity.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    override fun openUrl(activity: Activity, url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        return try {
            activity.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
