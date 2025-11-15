package com.example.yandexmedia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class TrackAdapter(private val tracks: ArrayList<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeTextView)
        val artworkImageView: ShapeableImageView = view.findViewById(R.id.artworkImageView)
        val arrowRightImageView: ImageView = view.findViewById(R.id.arrowRightImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_list_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.trackNameTextView.text = track.trackName
        holder.artistNameTextView.text = track.artistName
        holder.trackTimeTextView.text = track.trackTime

        Glide.with(holder.itemView.context)
            .load(track.artworkUrl100)
            .into(holder.artworkImageView)
    }

    override fun getItemCount(): Int = tracks.size

    // 🔹 метод для обновления списка
    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}
