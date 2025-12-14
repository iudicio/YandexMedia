package com.example.yandexmedia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import android.widget.Button


class TrackAdapter(
    private val tracks: ArrayList<Track>,
    private val onTrackClick: ((Track) -> Unit)? = null,
    private val onClearHistoryClick: (() -> Unit)? = null,
    private val showFooter: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TYPE_TRACK = 0
        const val TYPE_CLEAR = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (showFooter && position == tracks.size) TYPE_CLEAR else TYPE_TRACK
    }

    override fun getItemCount(): Int = tracks.size + if (showFooter) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CLEAR -> {
                val view = inflater.inflate(R.layout.item_clear_history, parent, false)
                ClearViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.track_list_item, parent, false)
                TrackViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrackViewHolder -> {
                val track = tracks[position]
                holder.trackNameTextView.text = track.trackName
                holder.artistNameTextView.text = track.artistName
                holder.trackTimeTextView.text = track.trackTime

                Glide.with(holder.itemView.context)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.artworkImageView)

                holder.itemView.setOnClickListener { onTrackClick?.invoke(track) }
            }

            is ClearViewHolder -> {
                holder.btn.setOnClickListener { onClearHistoryClick?.invoke() }
            }
        }
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackNameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = view.findViewById(R.id.artistNameTextView)
        val trackTimeTextView: TextView = view.findViewById(R.id.trackTimeTextView)
        val artworkImageView: ShapeableImageView = view.findViewById(R.id.artworkImageView)
        val arrowRightImageView: ImageView = view.findViewById(R.id.arrowRightImageView)
    }

    class ClearViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btn: Button = view.findViewById(R.id.btnClearHistory)
    }
}

