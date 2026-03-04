package com.example.yandexmedia.presentation.ui.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.yandexmedia.presentation.ui.media.favorites.FavoritesTracksFragment
import com.example.yandexmedia.presentation.ui.media.playlists.PlaylistsFragment

class MediaLibraryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}