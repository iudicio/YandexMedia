package com.example.yandexmedia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchActivity : AppCompatActivity() {

    // 🔹 Глобальная переменная для хранения текста поискового запроса
    private var searchQueryText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        // Подсветка текущего пункта меню
        bottomNav.selectedItemId = R.id.navigation_search

        // Кнопка Назад
        backButton.setOnClickListener {
            finish()
        }

        // Кнопка Очистить
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard(searchEditText)
        }

        // 🔹 Добавляем TextWatcher для отслеживания изменений текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 🔹 Обновляем переменную при изменении текста
                searchQueryText = s?.toString() ?: ""

                // Логика отображения кнопки очистки
                clearButton.visibility =
                    if (!s.isNullOrEmpty()) ImageView.VISIBLE else ImageView.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 🔹 Если есть сохранённый текст (при пересоздании Activity), восстановим его
        if (searchQueryText.isNotEmpty()) {
            searchEditText.setText(searchQueryText)
            searchEditText.setSelection(searchQueryText.length)
        }

        // Обработка нижней навигации
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> true
                R.id.navigation_library -> {
                    startActivity(Intent(this, MediaActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    // 🔹 Сохранение состояния при изменении конфигурации
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY_TEXT", searchQueryText)
    }

    // 🔹 Восстановление состояния при пересоздании Activity
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQueryText = savedInstanceState.getString("SEARCH_QUERY_TEXT", "")

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)

        searchEditText.setText(searchQueryText)
        searchEditText.setSelection(searchQueryText.length)
        clearButton.visibility =
            if (searchQueryText.isNotEmpty()) ImageView.VISIBLE else ImageView.GONE
    }

    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}
