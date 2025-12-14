package com.example.yandexmedia

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.net.UnknownHostException
import android.content.Intent

class SearchActivity : AppCompatActivity() {

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var searchRecyclerView: RecyclerView

    private lateinit var placeholderLayout: LinearLayout
    private lateinit var emptyImageView: ImageView
    private lateinit var emptyTextView: TextView
    private lateinit var networkErrorLayout: LinearLayout
    private lateinit var retryButton: Button

    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView

    private var searchQueryText: String = ""

    private val ioScope = CoroutineScope(Dispatchers.IO)
    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"
    }
    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(EXTRA_TRACK, track)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initHistory()
        initSearchList()
        initListeners()

        searchEditText.requestFocus()
        showHistoryIfNeeded()
    }

    private fun initViews() {
        val backButton = findViewById<ImageView>(R.id.backButton)

        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)

        searchRecyclerView = findViewById(R.id.searchResultsRecyclerView)

        placeholderLayout = findViewById(R.id.placeholderLayout)
        emptyImageView = findViewById(R.id.emptyImageView)
        emptyTextView = findViewById(R.id.emptyTextView)

        networkErrorLayout = findViewById(R.id.networkErrorLayout)
        retryButton = findViewById(R.id.retryButton)

        historyContainer = findViewById(R.id.historyContainer)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        backButton.setOnClickListener { finish() }
    }


    private fun initHistory() {
        val prefs = getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)

        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = TrackAdapter(
            arrayListOf(),
            onTrackClick = { track ->
                openPlayer(track)
            },
            onClearHistoryClick = {
                searchHistory.clear()
                historyAdapter.updateTracks(emptyList())
                historyContainer.isVisible = false
            },
            showFooter = true
        )

        historyRecyclerView.adapter = historyAdapter
    }



    private fun initSearchList() {
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(
            arrayListOf(),
            onTrackClick = { track ->
                searchHistory.addTrack(track)
                openPlayer(track)
            },
            showFooter = false
        )
        searchRecyclerView.adapter = searchAdapter
    }



    private fun initListeners() {

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard(searchEditText)
            searchAdapter.updateTracks(emptyList())
            showHistoryIfNeeded()
        }

        retryButton.setOnClickListener {
            if (searchQueryText.length > 2) {
                searchTracks(searchQueryText)
            } else {
                showHistoryIfNeeded()
            }
        }

        searchEditText.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                showHistoryIfNeeded()
            } else {
                historyContainer.isVisible = false
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQueryText = s?.toString() ?: ""
                clearButton.isVisible = !s.isNullOrEmpty()

                if (searchQueryText.length > 2) {
                    historyContainer.isVisible = false
                    searchTracks(searchQueryText)
                } else {
                    searchAdapter.updateTracks(emptyList())
                    if (searchEditText.hasFocus()) {
                        showHistoryIfNeeded()
                    } else {
                        showDefaultState()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun showHistoryIfNeeded() {
        val history = searchHistory.getHistory()
        val shouldShow = searchEditText.hasFocus() &&
                searchEditText.text.isEmpty() &&
                history.isNotEmpty()

        if (shouldShow) {
            historyAdapter.updateTracks(history)
            historyContainer.isVisible = true

            searchRecyclerView.isVisible = false
            placeholderLayout.isVisible = false
            networkErrorLayout.isVisible = false
        } else {
            historyContainer.isVisible = false
        }
    }

    private fun searchTracks(query: String) {
        ioScope.launch {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val url = URL("https://itunes.apple.com/search?entity=song&attribute=songTerm&limit=25&term=$encodedQuery")

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 1000
                connection.readTimeout = 1000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }
                    val trackList = parseTracks(response)
                    withContext(Dispatchers.Main) {
                        historyContainer.isVisible = false
                        searchAdapter.updateTracks(trackList)
                        showResultState(trackList.isEmpty())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showNetworkError()
                    }
                }
                connection.disconnect()

            } catch (e: UnknownHostException) {
                withContext(Dispatchers.Main) { showNetworkError() }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { showNetworkError() }
            }
        }
    }

    private fun parseTracks(json: String): List<Track> {
        val resultList = mutableListOf<Track>()
        val jsonObject = JSONObject(json)
        val results = jsonObject.getJSONArray("results")

        for (i in 0 until results.length()) {
            val trackObj = results.getJSONObject(i)

            val trackName = trackObj.optString("trackName", "Без названия")
            val artistName = trackObj.optString("artistName", "Неизвестен")
            val trackTimeMillis = trackObj.optLong("trackTimeMillis", 0)
            val artworkUrl100 = trackObj.optString("artworkUrl100", "")

            val collectionName = trackObj.optString("collectionName", "")
            val releaseDate = trackObj.optString("releaseDate", "") // ISO строка
            val primaryGenreName = trackObj.optString("primaryGenreName", "")
            val country = trackObj.optString("country", "")

            val trackId = trackObj.optLong("trackId", 0)

            resultList.add(
                Track(
                    trackId = trackId,
                    trackName = trackName,
                    artistName = artistName,
                    trackTime = millisToTime(trackTimeMillis),
                    artworkUrl100 = artworkUrl100,
                    collectionName = collectionName,
                    releaseDate = releaseDate,
                    primaryGenreName = primaryGenreName,
                    country = country,
                    trackTimeMillis = trackTimeMillis
                )
            )


        }
        return resultList
    }


    private fun millisToTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    /** UI-хелперы */

    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun showResultState(isEmpty: Boolean) {
        searchRecyclerView.isVisible = !isEmpty
        placeholderLayout.isVisible = isEmpty
        networkErrorLayout.isVisible = false
    }

    private fun showNetworkError() {
        searchRecyclerView.isVisible = false
        placeholderLayout.isVisible = false
        networkErrorLayout.isVisible = true
        historyContainer.isVisible = false
    }

    private fun showDefaultState() {
        searchRecyclerView.isVisible = false
        placeholderLayout.isVisible = false
        networkErrorLayout.isVisible = false
        historyContainer.isVisible = false
    }
}
