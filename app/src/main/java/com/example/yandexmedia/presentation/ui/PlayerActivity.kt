package com.example.yandexmedia.presentation.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.viewmodel.PlayerState
import com.example.yandexmedia.presentation.viewmodel.PlayerViewModel

class PlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModels()

    private lateinit var playButton: ImageButton
    private lateinit var positionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_player)

        val track = intent.getParcelableExtra<Track>(SearchActivity.EXTRA_TRACK)
            ?: return finish()

        bindTrack(track)
        observeState()

        viewModel.prepare(track.previewUrl)
    }

    private fun bindTrack(track: Track) {
        playButton = findViewById(R.id.playButton)
        positionText = findViewById(R.id.playbackPosition)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.lengthValue).text = track.trackTime.ifBlank { "—" }

        findViewById<TextView>(R.id.albumValue).text =
            track.collectionName?.takeIf { it.isNotBlank() } ?: "—"
        findViewById<TextView>(R.id.genreValue).text =
            track.primaryGenreName?.takeIf { it.isNotBlank() } ?: "—"
        findViewById<TextView>(R.id.countryValue).text =
            track.country?.takeIf { it.isNotBlank() } ?: "—"
        findViewById<TextView>(R.id.yearValue).text =
            track.releaseDate?.take(4)?.takeIf { it.length == 4 } ?: "—"

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .into(findViewById<ImageView>(R.id.coverImage))

        playButton.setOnClickListener { viewModel.onPlayPause() }
    }

    private fun observeState() {
        viewModel.state.observe(this) { state ->
            when (state) {
                PlayerState.Idle -> {
                    playButton.isEnabled = false
                    positionText.text = "00:00"
                    playButton.setImageResource(R.drawable.play)
                }

                PlayerState.Prepared -> {
                    playButton.isEnabled = true
                    positionText.text = "00:00"
                    playButton.setImageResource(R.drawable.play)
                }

                is PlayerState.Playing -> {
                    playButton.isEnabled = true
                    positionText.text = state.position
                    playButton.setImageResource(R.drawable.stop)
                }

                is PlayerState.Paused -> {
                    playButton.isEnabled = true
                    positionText.text = state.position
                    playButton.setImageResource(R.drawable.play)
                }

                PlayerState.Completed -> {
                    playButton.isEnabled = true
                    positionText.text = "00:00"
                    playButton.setImageResource(R.drawable.play)
                }

                is PlayerState.Error -> {
                    playButton.isEnabled = false
                    positionText.text = "00:00"
                    playButton.setImageResource(R.drawable.play)
                }
            }
        }
    }
}
