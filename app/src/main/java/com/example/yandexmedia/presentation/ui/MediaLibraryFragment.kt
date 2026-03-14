package com.example.yandexmedia.presentation.ui.media

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.ui.media.viewmodel.MediaLibraryViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryFragment : Fragment(R.layout.fragment_media_library) {

    private val viewModel: MediaLibraryViewModel by viewModel()

    private var tabMediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = MediaLibraryPagerAdapter(this)
        viewPager.offscreenPageLimit = 1

        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_favorites)
                else -> getString(R.string.tab_playlists)
            }
        }.also { it.attach() }
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }
}