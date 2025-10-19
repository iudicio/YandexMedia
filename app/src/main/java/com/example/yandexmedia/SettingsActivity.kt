package com.example.yandexmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val darkSwitch = findViewById<Switch>(R.id.darkThemeSwitch)
        val shareApp = findViewById<LinearLayout>(R.id.shareApp)
        val support = findViewById<LinearLayout>(R.id.support)
        val userAgreement = findViewById<LinearLayout>(R.id.userAgreement)

        // Получаем сохранённое состояние темы
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_theme", false)
        darkSwitch.isChecked = isDark

        // Кнопка Назад
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Переключатель темы
        darkSwitch.setOnCheckedChangeListener { _, checked ->
            ThemeManager.toggleTheme(this, checked)
        }

        // 🔹 Кнопка «Поделиться приложением»
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

        // 🔹 Кнопка «Написать разработчикам»
        support.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822" // только почтовые клиенты
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_to)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            }

            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email_chooser_title)))
            } catch (ex: android.content.ActivityNotFoundException) {
                android.widget.Toast.makeText(
                    this,
                    getString(R.string.no_email_client),
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }

        // 🔹 Кнопка «Пользовательское соглашение»
        userAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.offer_link))
            }
            startActivity(browserIntent)
        }
    }
}
