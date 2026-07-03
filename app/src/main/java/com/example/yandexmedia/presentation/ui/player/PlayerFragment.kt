package com.example.yandexmedia.presentation.ui.player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.player.PlayerService
import com.example.yandexmedia.presentation.adapter.PlaylistBottomSheetAdapter
import com.example.yandexmedia.presentation.viewmodel.PlayerState
import com.example.yandexmedia.presentation.viewmodel.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var playButton: PlaybackButtonView
    private lateinit var favoriteButton: ImageButton
    private lateinit var positionText: TextView
    private var track: Track? = null
    private var isServiceBound = false

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as? PlayerService.PlayerBinder)?.getService() ?: return
            val currentTrack = track ?: return
            isServiceBound = true
            viewModel.onServiceConnected(service, currentTrack)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = arguments?.getParcelable<Track>("track") ?: return
        this.track = track

        bindTrack(view, track)
        observeState()
        requestNotificationPermission()
        bindPlayerService(track)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onUiForegrounded()
    }

    override fun onStop() {
        if (!requireActivity().isChangingConfigurations) {
            viewModel.onUiBackgrounded(notificationsAllowed())
        }
        super.onStop()
    }

    override fun onDestroyView() {
        viewModel.release()
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }
        track = null
        super.onDestroyView()
    }

    private fun bindPlayerService(track: Track) {
        val intent = Intent(requireContext(), PlayerService::class.java).apply {
            putExtra(PlayerService.EXTRA_TRACK, track)
        }
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun notificationsAllowed(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    private fun bindTrack(view: View, track: Track) {
        playButton = view.findViewById(R.id.playButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        positionText = view.findViewById(R.id.playbackPosition)

        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<TextView>(R.id.trackName).text = track.trackName
        view.findViewById<TextView>(R.id.artistName).text = track.artistName
        view.findViewById<TextView>(R.id.lengthValue).text =
            track.trackTime.ifBlank { "—" }

        view.findViewById<ImageButton>(R.id.addToPlaylistButton).setOnClickListener {
            showAddToPlaylistBottomSheet(track)
        }

        view.findViewById<TextView>(R.id.albumValue).text =
            track.collectionName?.takeIf { it.isNotBlank() } ?: "—"
        view.findViewById<TextView>(R.id.genreValue).text =
            track.primaryGenreName?.takeIf { it.isNotBlank() } ?: "—"
        view.findViewById<TextView>(R.id.countryValue).text =
            track.country?.takeIf { it.isNotBlank() } ?: "—"
        view.findViewById<TextView>(R.id.yearValue).text =
            track.releaseDate?.take(4)?.takeIf { it.length == 4 } ?: "—"

        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .into(view.findViewById<ImageView>(R.id.coverImage))

        playButton.setOnClickListener {
            viewModel.onPlayPause()
        }

        favoriteButton.setOnClickListener {
            viewModel.onFavouriteClicked()
        }
    }

    private fun showAddToPlaylistBottomSheet(track: Track) {
        val dialog = BottomSheetDialog(requireContext())
        val contentView = layoutInflater.inflate(
            R.layout.bottom_sheet_add_to_playlist,
            null
        )

        val recyclerView = contentView.findViewById<RecyclerView>(
            R.id.playlistsRecyclerView
        )
        val newPlaylistButton = contentView.findViewById<ImageButton>(
            R.id.newPlaylistButton
        )

        val adapter = PlaylistBottomSheetAdapter { playlist ->
            viewModel.addTrackToPlaylist(
                playlistId = playlist.id,
                track = track
            ) { isAdded ->
                if (isAdded) {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Добавлено в плейлист ${playlist.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Трек уже добавлен в плейлист ${playlist.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.updatePlaylists(playlists)
        }

        newPlaylistButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.createPlaylistFragment)
        }

        dialog.setContentView(contentView)
        dialog.show()
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            playButton.isEnabled = state.isPlayButtonEnabled
            positionText.text = state.currentPosition

            playButton.setPlaying(
                state.playbackState == PlayerState.PlaybackState.Playing
            )

            updateFavouriteButton(state.isFavourite)
        }
    }

    private fun updateFavouriteButton(isFavourite: Boolean) {
        val iconRes = if (isFavourite) {
            R.drawable.like_button_active
        } else {
            R.drawable.like_button_inactive
        }

        favoriteButton.setImageResource(iconRes)
    }
}
