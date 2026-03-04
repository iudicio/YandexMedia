package com.example.yandexmedia.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.ui.media.MediaLibraryPagerAdapter
import com.example.yandexmedia.presentation.ui.media.viewmodel.MediaLibraryViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaActivity : AppCompatActivity() {

    private val viewModel: MediaLibraryViewModel by viewModel()

    private lateinit var backButton: ImageButton
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private var tabMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        setupUi()
        setupBackHandling()
    }

    private fun setupUi() {
        backButton = findViewById(R.id.btnBack)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        viewPager.adapter = MediaLibraryPagerAdapter(this)
        viewPager.offscreenPageLimit = 1

        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_favorites)
                else -> getString(R.string.tab_playlists)
            }
        }.also { it.attach() }
    }

    private fun setupBackHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                openMain()
            }
        })
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onDestroy() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroy()
    }
}