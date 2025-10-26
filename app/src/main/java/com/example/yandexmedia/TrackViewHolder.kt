package com.example.yandexmedia

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val artworkImageView: ImageView = view.findViewById(R.id.artworkImageView)
    private val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
    private val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
    private val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeTextView)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackTimeTextView.text = track.trackTime

        // Используем Glide с плейсхолдером и скруглением
        val requestOptions = RequestOptions()
            .transform(RoundedCorners(16)) // скругление углов
            .placeholder(R.drawable.ic_placeholder) // показывается, пока грузится
            .error(R.drawable.ic_placeholder) // показывается при ошибке (например, нет интернета)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .apply(requestOptions)
            .into(artworkImageView)
    }
}
