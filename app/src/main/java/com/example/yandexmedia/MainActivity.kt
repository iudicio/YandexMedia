package com.example.yandexmedia

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // применяем сохранённую тему
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)

        // подключаем XML-разметку
        setContentView(R.layout.activity_main)

        // находим карточки по id
        val cardSearch = findViewById<LinearLayout>(R.id.card_search)
        val cardLibrary = findViewById<LinearLayout>(R.id.card_library)
        val cardSettings = findViewById<LinearLayout>(R.id.card_settings)

        // обработка кликов
        cardSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        cardLibrary.setOnClickListener {
            val intent = Intent(this, MediaActivity::class.java)
            startActivity(intent)
        }

        cardSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
