package com.example.yandexmedia.presentation.ui.media.playlists

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.adapter.PlaylistAdapter
import com.example.yandexmedia.presentation.ui.media.model.PlaylistsState
import com.example.yandexmedia.presentation.ui.media.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment(R.layout.fragment_playlists) {

    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var adapter: PlaylistAdapter
    private lateinit var playlistsRecyclerView: RecyclerView
    private lateinit var emptyPlaylistsContainer: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsRecyclerView = view.findViewById(R.id.playlistsRecyclerView)
        emptyPlaylistsContainer = view.findViewById(R.id.emptyPlaylistsContainer)

        adapter = PlaylistAdapter()

        playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistsRecyclerView.adapter = adapter

        view.findViewById<ImageButton>(R.id.btnNewPlaylist).setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_createPlaylistFragment
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlaylistsState.Empty -> {
                    playlistsRecyclerView.isVisible = false
                    emptyPlaylistsContainer.isVisible = true
                }

                is PlaylistsState.Content -> {
                    playlistsRecyclerView.isVisible = true
                    emptyPlaylistsContainer.isVisible = false
                    adapter.updatePlaylists(state.playlists)
                }
            }
        }
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment().apply {
            arguments = Bundle()
        }
    }
}