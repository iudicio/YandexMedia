package com.example.yandexmedia.presentation.ui

import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import com.example.yandexmedia.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardSearch = findViewById<MaterialButton>(R.id.card_search)
        val cardLibrary = findViewById<MaterialButton>(R.id.card_library)
        val cardSettings = findViewById<MaterialButton>(R.id.card_settings)

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
