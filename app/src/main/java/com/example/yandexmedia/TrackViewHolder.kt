package com.example.yandexmedia

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val artworkImageView: ImageView = view.findViewById(R.id.artworkImageView)
    private val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
    private val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
    private val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeTextView)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.trackTime

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .into(artworkImageView)
    }
}
