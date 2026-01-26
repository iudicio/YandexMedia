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

    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false
    private var completed = false

    private lateinit var playButton: ImageButton
    private lateinit var positionText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val updater = object : Runnable {
        override fun run() {
            positionText.text = format(mediaPlayer?.currentPosition ?: 0)
            handler.postDelayed(this, 300)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_player)

        val track = intent.getParcelableExtra<Track>(SearchActivity.EXTRA_TRACK)
            ?: return finish()

        bindTrack(track)
        prepare(track.previewUrl)
    }

    private fun bindTrack(track: Track) {
        playButton = findViewById(R.id.playButton)
        positionText = findViewById(R.id.playbackPosition)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .into(findViewById<ImageView>(R.id.coverImage))

        playButton.setOnClickListener { toggle() }
    }

    private fun prepare(url: String) {
        if (url.isBlank()) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { prepared = true }
            setOnCompletionListener {
                completed = true
                stopUpdates()
                playButton.setImageResource(R.drawable.play)
                positionText.text = "00:00"
            }
            prepareAsync()
        }
    }

    private fun toggle() {
        val player = mediaPlayer ?: return
        if (!prepared) return

        if (player.isPlaying) {
            player.pause()
            stopUpdates()
            playButton.setImageResource(R.drawable.play)
        } else {
            if (completed) {
                player.seekTo(0)
                completed = false
            }
            player.start()
            startUpdates()
            playButton.setImageResource(R.drawable.stop)
        }
    }

    private fun startUpdates() {
        handler.post(updater)
    }

    private fun stopUpdates() {
        handler.removeCallbacks(updater)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun format(ms: Int): String {
        val s = ms / 1000
        return String.format("%02d:%02d", s / 60, s % 60)
    }
}
