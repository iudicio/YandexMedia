package com.example.yandexmedia.presentation.ui.media.favorites

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.adapter.TrackAdapter
import com.example.yandexmedia.presentation.ui.media.model.FavoritesTracksState
import com.example.yandexmedia.presentation.ui.media.viewmodel.FavoritesTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesTracksFragment : Fragment(R.layout.fragment_favorites_tracks) {

    private val viewModel: FavoritesTracksViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var adapter: TrackAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favoriteTracksRecyclerView)
        placeholderImage = view.findViewById(R.id.placeholderImage)
        placeholderText = view.findViewById(R.id.placeholderText)

        adapter = TrackAdapter(
            tracks = arrayListOf(),
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_mediaLibraryFragment_to_playerFragment,
                    bundleOf("track" to track)
                )
            },
            showFooter = false
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                FavoritesTracksState.Empty -> {
                    recyclerView.isVisible = false
                    placeholderImage.isVisible = true
                    placeholderText.isVisible = true
                }

                is FavoritesTracksState.Content -> {
                    recyclerView.isVisible = true
                    placeholderImage.isVisible = false
                    placeholderText.isVisible = false
                    adapter.updateTracks(state.tracks)
                }
            }
        }
    }

    companion object {
        fun newInstance(): FavoritesTracksFragment = FavoritesTracksFragment().apply {
            arguments = Bundle()
        }
    }
}