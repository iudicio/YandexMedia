package com.example.yandexmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Применяем сохранённую тему
        applySavedTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val darkSwitch = findViewById<Switch>(R.id.darkThemeSwitch)
        val shareApp = findViewById<LinearLayout>(R.id.shareApp)
        val support = findViewById<LinearLayout>(R.id.support)
        val userAgreement = findViewById<LinearLayout>(R.id.userAgreement)

        // Загружаем сохранённое состояние
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)
        darkSwitch.isChecked = isDark

        // Назад
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Переключение темы
        darkSwitch.setOnCheckedChangeListener { _, checked ->
            val editor = prefs.edit()
            editor.putBoolean("dark_theme", checked)
            editor.apply()

            // Меняем тему
            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Поделиться приложением
        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.share_message, getString(R.string.course_link))
                )
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        }

        // Написать разработчикам
        support.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_to)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            }

            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email_chooser_title)))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(R.string.no_email_client),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Пользовательское соглашение
        userAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.offer_link))
            }
            startActivity(browserIntent)
        }
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
