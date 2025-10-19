package com.example.yandexmedia

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MediaActivity : AppCompatActivity() {

    private lateinit var tabFavorites: TextView
    private lateinit var tabPlaylists: TextView
    private lateinit var tabIndicator: View

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_library

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navigation_library -> true
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        tabFavorites = findViewById(R.id.tabFavorites)
        tabPlaylists = findViewById(R.id.tabPlaylists)
        tabIndicator = findViewById(R.id.tabIndicator)

        tabFavorites.setOnClickListener { selectTab(it as TextView) }
        tabPlaylists.setOnClickListener { selectTab(it as TextView) }

        tabFavorites.post {
            moveIndicatorTo(tabFavorites, animate = false)
            highlightTab(tabFavorites)
        }
    }

    private fun moveIndicatorTo(tab: TextView, animate: Boolean) {
        val textPaint: TextPaint = tab.paint
        val textWidth = textPaint.measureText(tab.text.toString())
        val textStartX = tab.x + (tab.width - textWidth) / 2f

        if (animate) {
            ObjectAnimator.ofFloat(tabIndicator, "x", textStartX).apply {
                duration = 250
                start()
            }
            ObjectAnimator.ofInt(tabIndicator, "width", textWidth.toInt()).apply {
                duration = 250
                start()
            }
        } else {
            tabIndicator.x = textStartX
            tabIndicator.layoutParams.width = textWidth.toInt()
            tabIndicator.requestLayout()
        }
    }

    private fun selectTab(selectedTab: TextView) {
        moveIndicatorTo(selectedTab, animate = true)
        highlightTab(selectedTab)
    }

    private fun highlightTab(activeTab: TextView) {
        val inactiveColor = ContextCompat.getColor(this, R.color.color_gray_dark)
        val activeColor = ContextCompat.getColor(this, R.color.color_black)
        tabFavorites.setTextColor(inactiveColor)
        tabPlaylists.setTextColor(inactiveColor)
        activeTab.setTextColor(activeColor)
    }
}
