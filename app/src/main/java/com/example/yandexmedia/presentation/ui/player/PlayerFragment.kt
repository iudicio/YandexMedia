package com.example.yandexmedia.presentation.ui.player

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.viewmodel.PlayerState
import com.example.yandexmedia.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var playButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var positionText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = arguments?.getParcelable<Track>("track") ?: return

        bindTrack(view, track)
        observeState()
        viewModel.prepare(track)
    }

    override fun onDestroyView() {
        viewModel.release()
        super.onDestroyView()
    }

    private fun bindTrack(view: View, track: Track) {
        playButton = view.findViewById(R.id.playButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        positionText = view.findViewById(R.id.playbackPosition)

        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<TextView>(R.id.trackName).text = track.trackName
        view.findViewById<TextView>(R.id.artistName).text = track.artistName
        view.findViewById<TextView>(R.id.lengthValue).text = track.trackTime.ifBlank { "—" }

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

        playButton.setOnClickListener { viewModel.onPlayPause() }
        favoriteButton.setOnClickListener { viewModel.onFavouriteClicked() }
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            playButton.isEnabled = state.isPlayButtonEnabled
            positionText.text = state.currentPosition

            when (state.playbackState) {
                PlayerState.PlaybackState.Idle -> {
                    playButton.setImageResource(R.drawable.play)
                }

                PlayerState.PlaybackState.Prepared -> {
                    playButton.setImageResource(R.drawable.play)
                }

                PlayerState.PlaybackState.Playing -> {
                    playButton.setImageResource(R.drawable.stop)
                }

                PlayerState.PlaybackState.Paused -> {
                    playButton.setImageResource(R.drawable.play)
                }

                PlayerState.PlaybackState.Completed -> {
                    playButton.setImageResource(R.drawable.play)
                }

                PlayerState.PlaybackState.Error -> {
                    playButton.setImageResource(R.drawable.play)
                }
            }

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