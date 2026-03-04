package com.example.yandexmedia.presentation.ui.media.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.ui.media.viewmodel.FavoritesTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment(R.layout.fragment_favorites_tracks) {

    private val viewModel: FavoritesTracksViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): FavoritesTracksFragment = FavoritesTracksFragment().apply {
            arguments = Bundle()
        }
    }
}