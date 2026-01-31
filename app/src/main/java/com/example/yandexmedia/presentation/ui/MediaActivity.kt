package com.example.yandexmedia.presentation.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.yandexmedia.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MediaActivity : AppCompatActivity() {

    private lateinit var tabFavorites: TextView
    private lateinit var tabPlaylists: TextView
    private lateinit var indicator: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        setupBottomNavigation()
        setupTabs()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.navigation_library

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    open(SearchActivity::class.java)
                    true
                }
                R.id.navigation_settings -> {
                    open(SettingsActivity::class.java)
                    true
                }
                else -> true
            }
        }
    }

    private fun open(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
        overridePendingTransition(0, 0)
        finish()
    }

    private fun setupTabs() {
        tabFavorites = findViewById(R.id.tabFavorites)
        tabPlaylists = findViewById(R.id.tabPlaylists)
        indicator = findViewById(R.id.tabIndicator)

        tabFavorites.setOnClickListener { selectTab(it as TextView) }
        tabPlaylists.setOnClickListener { selectTab(it as TextView) }

        tabFavorites.post {
            moveIndicator(tabFavorites, false)
            highlight(tabFavorites)
        }
    }

    private fun selectTab(tab: TextView) {
        moveIndicator(tab, true)
        highlight(tab)
    }

    private fun moveIndicator(tab: TextView, animate: Boolean) {
        val paint: TextPaint = tab.paint
        val width = paint.measureText(tab.text.toString())
        val x = tab.x + (tab.width - width) / 2f

        if (animate) {
            ObjectAnimator.ofFloat(indicator, "x", x).setDuration(250).start()
            ObjectAnimator.ofInt(indicator, "width", width.toInt()).setDuration(250).start()
        } else {
            indicator.x = x
            indicator.layoutParams.width = width.toInt()
            indicator.requestLayout()
        }
    }

    private fun highlight(active: TextView) {
        val inactive = ContextCompat.getColor(this, R.color.color_gray_dark)
        val activeColor = ContextCompat.getColor(this, R.color.color_black)

        tabFavorites.setTextColor(inactive)
        tabPlaylists.setTextColor(inactive)
        active.setTextColor(activeColor)
    }
}
