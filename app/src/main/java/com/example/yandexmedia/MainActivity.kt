package com.example.yandexmedia

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardSearch = findViewById<LinearLayout>(R.id.card_search)
        val cardLibrary = findViewById<LinearLayout>(R.id.card_library)
        val cardSettings = findViewById<LinearLayout>(R.id.card_settings)

        cardSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        cardLibrary.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        cardSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
