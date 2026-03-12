package com.example.yandexmedia.presentation.ui.media.playlists

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.ui.media.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Пока логики нет — заглушка по дизайну
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment().apply {
            arguments = Bundle()
        }
    }
}