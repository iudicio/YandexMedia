package com.example.yandexmedia.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Playlist

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)

        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(
            playlist = playlists[position],
            onPlaylistClick = onPlaylistClick
        )
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val coverImage: ImageView = view.findViewById(R.id.playlistCoverImage)
        private val nameText: TextView = view.findViewById(R.id.playlistNameText)
        private val countText: TextView = view.findViewById(R.id.playlistTracksCountText)

        fun bind(
            playlist: Playlist,
            onPlaylistClick: (Playlist) -> Unit
        ) {
            nameText.text = playlist.name
            countText.text = "${playlist.tracksCount} треков"

            coverImage.post {
                val size = coverImage.width

                if (size > 0 && coverImage.layoutParams.height != size) {
                    coverImage.layoutParams = coverImage.layoutParams.apply {
                        height = size
                    }
                }
            }

            Glide.with(itemView.context)
                .load(playlist.coverPath)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .centerCrop()
                .into(coverImage)

            itemView.setOnClickListener {
                onPlaylistClick(playlist)
            }
        }
    }
}