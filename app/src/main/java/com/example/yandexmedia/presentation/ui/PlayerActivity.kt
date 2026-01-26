package com.example.yandexmedia.presentation.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track

class PlayerActivity : AppCompatActivity() {

    private var isFavorite = false

    private var mediaPlayer: MediaPlayer? = null

    private var isPlayerPrepared = false
    private var isTrackCompleted = false

    private val handler = Handler(Looper.getMainLooper())
    private val updatePositionRunnable = object : Runnable {
        override fun run() {
            val currentMs = mediaPlayer?.currentPosition ?: 0
            playbackPositionTextView?.text = formatTime(currentMs)
            handler.postDelayed(this, 300L)
        }
    }

    private var playbackPositionTextView: TextView? = null
    private var playButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_player)

        val track = intent.getParcelableExtra<Track>(SearchActivity.EXTRA_TRACK)
            ?: run { finish(); return }

        val backButton: ImageButton = findViewById(R.id.backButton)
        val coverImage: ImageView = findViewById(R.id.coverImage)
        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)
        val lengthValue: TextView = findViewById(R.id.lengthValue)
        val albumValue: TextView = findViewById(R.id.albumValue)
        val yearValue: TextView = findViewById(R.id.yearValue)
        val genreValue: TextView = findViewById(R.id.genreValue)
        val countryValue: TextView = findViewById(R.id.countryValue)

        playButton = findViewById(R.id.playButton)
        playbackPositionTextView = findViewById(R.id.playbackPosition)

        val favoriteButton: ImageButton = findViewById(R.id.favoriteButton)
        val addToPlaylistButton: ImageButton = findViewById(R.id.addToPlaylistButton)

        trackName.text = track.trackName
        artistName.text = track.artistName
        lengthValue.text = track.trackTime
        albumValue.text = track.collectionName ?: ""
        yearValue.text = track.releaseDate?.take(4) ?: ""
        genreValue.text = track.primaryGenreName ?: ""
        countryValue.text = track.country ?: ""

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(coverImage)

        setPlayButtonState(isPlaying = false)
        playbackPositionTextView?.text = "00:00"

        val previewUrl = track.previewUrl

        if (previewUrl.isBlank()) {
            playButton?.isEnabled = false
            playbackPositionTextView?.text = "00:00"
            playButton?.setImageResource(R.drawable.play)

        } else {
            preparePlayer(previewUrl)
        }


        playButton?.setOnClickListener {
            onPlayPauseClicked()
        }

        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.like)
            } else {
                favoriteButton.setImageResource(R.drawable.favorite)
            }
        }

        backButton.setOnClickListener {
            stopAndReleasePlayer()
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun preparePlayer(url: String) {
        releasePlayerOnly()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                isPlayerPrepared = true
            }
            setOnCompletionListener {
                isTrackCompleted = true
                stopProgressUpdates()
                playbackPositionTextView?.text = "00:00"
                setPlayButtonState(isPlaying = false)
            }
            prepareAsync()
        }
    }

    private fun onPlayPauseClicked() {
        val player = mediaPlayer ?: return

        if (!isPlayerPrepared) {
            // Ещё готовится — можно просто игнорировать нажатие
            return
        }

        if (player.isPlaying) {
            player.pause()
            stopProgressUpdates()
            setPlayButtonState(isPlaying = false)
        } else {
            if (isTrackCompleted) {
                player.seekTo(0)
                playbackPositionTextView?.text = "00:00"
                isTrackCompleted = false
            }
            player.start()
            setPlayButtonState(isPlaying = true)
            startProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        handler.removeCallbacks(updatePositionRunnable)
        handler.post(updatePositionRunnable)
    }

    private fun stopProgressUpdates() {
        handler.removeCallbacks(updatePositionRunnable)
    }

    private fun setPlayButtonState(isPlaying: Boolean) {
        playButton?.setImageResource(
            if (isPlaying) R.drawable.stop else R.drawable.play
        )
    }

    private fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onStop() {
        super.onStop()
        val player = mediaPlayer
        if (player != null && player.isPlaying) {
            player.pause()
            stopProgressUpdates()
            setPlayButtonState(isPlaying = false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndReleasePlayer()
    }

    private fun stopAndReleasePlayer() {
        stopProgressUpdates()
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) player.stop()
            } catch (_: IllegalStateException) {
            }
        }
        releasePlayerOnly()
        playbackPositionTextView?.text = "00:00"
        setPlayButtonState(isPlaying = false)
    }

    private fun releasePlayerOnly() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlayerPrepared = false
        isTrackCompleted = false
    }
}
