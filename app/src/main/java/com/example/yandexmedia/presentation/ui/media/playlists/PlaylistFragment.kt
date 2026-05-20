package com.example.yandexmedia.presentation.ui.media.playlists

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Playlist
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.adapter.TrackAdapter
import com.example.yandexmedia.presentation.ui.media.model.PlaylistScreenState
import com.example.yandexmedia.presentation.ui.media.viewmodel.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var coverImage: ImageView
    private lateinit var playlistNameText: TextView
    private lateinit var playlistDescriptionText: TextView
    private lateinit var playlistInfoText: TextView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var emptyText: TextView

    private lateinit var adapter: TrackAdapter

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = requireArguments().getLong("playlistId")

        initViews(view)
        setupBottomSheet(view)
        initRecyclerView()
        setupClicks(view)
        observeState()

        viewModel.loadPlaylist(playlistId)
    }

    private fun initViews(view: View) {
        coverImage = view.findViewById(R.id.playlistCoverImage)
        playlistNameText = view.findViewById(R.id.playlistNameText)
        playlistDescriptionText = view.findViewById(R.id.playlistDescriptionText)
        playlistInfoText = view.findViewById(R.id.playlistInfoText)
        tracksRecyclerView = view.findViewById(R.id.playlistTracksRecyclerView)
        emptyText = view.findViewById(R.id.emptyTracksText)
    }

    private fun setupBottomSheet(view: View) {
        val bottomSheet =
            view.findViewById<LinearLayout>(R.id.playlistTracksBottomSheet)

        val moreButton =
            view.findViewById<ImageButton>(R.id.btnMore)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            isHideable = false
            skipCollapsed = false
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        view.post {
            val screenHeight = view.height

            val moreButtonLocation = IntArray(2)
            val rootLocation = IntArray(2)

            moreButton.getLocationOnScreen(moreButtonLocation)
            view.getLocationOnScreen(rootLocation)

            val moreButtonBottom =
                moreButtonLocation[1] - rootLocation[1] + moreButton.height

            val margin = (8 * resources.displayMetrics.density).toInt()

            bottomSheetBehavior.peekHeight =
                screenHeight - moreButtonBottom - margin
        }

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int
                ) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_COLLAPSED
                    }
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float
                ) = Unit
            }
        )
    }
    private fun showDeleteTrackDialog(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ", null)
            .setPositiveButton("ДА") { _, _ ->
                viewModel.removeTrackFromPlaylist(track) {
                    Toast.makeText(
                        requireContext(),
                        "Трек удалён из плейлиста",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(requireContext().getColor(R.color.color_primary_permomently))

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            .setTextColor(requireContext().getColor(R.color.color_primary_permomently))
    }
    private fun initRecyclerView() {
        adapter = TrackAdapter(
            tracks = arrayListOf(),
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.playerFragment,
                    Bundle().apply {
                        putParcelable("track", track)
                    }
                )
            },
            onTrackLongClick = { track ->
                showDeleteTrackDialog(track)
            },
            showFooter = false
        )
        tracksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        tracksRecyclerView.adapter = adapter
    }

    private fun setupClicks(view: View) {

        view.findViewById<ImageButton>(R.id.btnBack)
            .setOnClickListener {

                findNavController().navigateUp()
            }

        view.findViewById<ImageButton>(R.id.btnShare)
            .setOnClickListener {

                sharePlaylist()
            }

        view.findViewById<ImageButton>(R.id.btnMore)
            .setOnClickListener {

                Toast.makeText(
                    requireContext(),
                    "Меню плейлиста",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun observeState() {

        viewModel.state.observe(viewLifecycleOwner) { state ->

            when (state) {

                PlaylistScreenState.Loading -> Unit

                PlaylistScreenState.NotFound -> {

                    Toast.makeText(
                        requireContext(),
                        "Плейлист не найден",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigateUp()
                }

                is PlaylistScreenState.Content -> {

                    currentPlaylist = state.playlist
                    currentTracks = state.tracks

                    renderContent(
                        state.playlist,
                        state.tracks
                    )
                }
            }
        }
    }

    private fun renderContent(
        playlist: Playlist,
        tracks: List<Track>
    ) {

        playlistNameText.text = playlist.name

        playlistDescriptionText.text =
            playlist.description

        playlistDescriptionText.isVisible =
            playlist.description.isNotBlank()

        playlistInfoText.text =
            "${getTotalMinutesText(tracks)} • ${
                getTracksCountText(tracks.size)
            }"

        Glide.with(requireContext())
            .load(playlist.coverPath)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .centerCrop()
            .into(coverImage)

        adapter.updateTracks(tracks)

        tracksRecyclerView.isVisible =
            tracks.isNotEmpty()

        emptyText.isVisible =
            tracks.isEmpty()
    }

    private fun sharePlaylist() {

        val playlist = currentPlaylist ?: return

        if (currentTracks.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "В этом плейлисте нет треков, которыми можно поделиться",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val text = buildString {

            appendLine(playlist.name)

            if (playlist.description.isNotBlank()) {
                appendLine(playlist.description)
            }

            appendLine(
                getTracksCountText(currentTracks.size)
            )

            currentTracks.forEachIndexed { index, track ->

                appendLine(
                    "${index + 1}. " +
                            "${track.artistName} - " +
                            "${track.trackName} " +
                            "(${track.trackTime})"
                )
            }
        }

        val intent = Intent(Intent.ACTION_SEND).apply {

            type = "text/plain"

            putExtra(
                Intent.EXTRA_TEXT,
                text
            )
        }

        startActivity(
            Intent.createChooser(
                intent,
                "Поделиться плейлистом"
            )
        )
    }

    private fun getTotalMinutesText(
        tracks: List<Track>
    ): String {

        val totalMillis =
            tracks.sumOf { it.trackTimeMillis ?: 0L }

        val minutes = totalMillis / 60000

        val word = when {

            minutes % 10 == 1L &&
                    minutes % 100 != 11L ->
                "минута"

            minutes % 10 in 2..4 &&
                    minutes % 100 !in 12..14 ->
                "минуты"

            else ->
                "минут"
        }

        return "$minutes $word"
    }

    private fun getTracksCountText(count: Int): String {

        val word = when {

            count % 10 == 1 &&
                    count % 100 != 11 ->
                "трек"

            count % 10 in 2..4 &&
                    count % 100 !in 12..14 ->
                "трека"

            else ->
                "треков"
        }

        return "$count $word"
    }
}