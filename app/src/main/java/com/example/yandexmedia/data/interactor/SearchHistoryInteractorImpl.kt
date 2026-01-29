package com.example.yandexmedia.domain.interactor

import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    companion object {
        private const val MAX_SIZE = 10
    }

    override fun getHistory(): List<Track> {
        val stored = repository.read()
        val filtered = stored.filter { it.previewUrl.isNotBlank() }

        if (filtered.size != stored.size) {
            repository.write(filtered)
        }

        return filtered
    }

    override fun addTrack(track: Track) {
        if (track.previewUrl.isBlank()) return

        val list = getHistory().toMutableList()
        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)

        val trimmed = if (list.size > MAX_SIZE) list.subList(0, MAX_SIZE) else list
        repository.write(trimmed)
    }

    override fun clear() {
        repository.clear()
    }
}
