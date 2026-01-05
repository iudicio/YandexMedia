package com.example.yandexmedia

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class PlayerActivity : AppCompatActivity() {
    private var isPlaying = false
    private var isFavorite = false
    private var isInPlaylist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_player)

        val track = intent.getParcelableExtra<Track>(SearchActivity.EXTRA_TRACK)
            ?: run {
                finish()
                return
            }

        val backButton: ImageButton = findViewById(R.id.backButton)
        val coverImage: ImageView = findViewById(R.id.coverImage)
        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)
        val lengthValue: TextView = findViewById(R.id.lengthValue)
        val albumValue: TextView = findViewById(R.id.albumValue)
        val yearValue: TextView = findViewById(R.id.yearValue)
        val genreValue: TextView = findViewById(R.id.genreValue)
        val countryValue: TextView = findViewById(R.id.countryValue)
        val playButton: ImageButton = findViewById(R.id.playButton)
        val favoriteButton: ImageButton = findViewById(R.id.favoriteButton)
        val addToPlaylistButton: ImageButton = findViewById(R.id.addToPlaylistButton)
        playButton.setOnClickListener {
            isPlaying = !isPlaying

            if (isPlaying) {
                playButton.setImageResource(R.drawable.play)
            } else {
                playButton.setImageResource(R.drawable.stop)
            }
        }
        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite

            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.like)
            } else {
                favoriteButton.setImageResource(R.drawable.favorite)
            }
        }

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

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
    }
}
