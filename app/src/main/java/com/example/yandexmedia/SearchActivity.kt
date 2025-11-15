package com.example.yandexmedia

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var emptyImageView: ImageView
    private lateinit var emptyTextView: TextView
    private lateinit var networkErrorLayout: LinearLayout
    private lateinit var retryButton: Button

    private var searchQueryText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        recyclerView = findViewById(R.id.searchResultsRecyclerView)
        placeholderLayout = findViewById(R.id.placeholderLayout)
        emptyImageView = findViewById(R.id.emptyImageView)
        emptyTextView = findViewById(R.id.emptyTextView)
        networkErrorLayout = findViewById(R.id.networkErrorLayout)
        retryButton = findViewById(R.id.retryButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(arrayListOf())
        recyclerView.adapter = adapter

        backButton.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard(searchEditText)
            adapter.updateTracks(emptyList())
            showDefaultState()
        }

        retryButton.setOnClickListener {
            if (searchQueryText.length > 2) {
                searchTracks(searchQueryText)
            } else {
                showDefaultState()
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQueryText = s?.toString() ?: ""
                clearButton.isVisible = !s.isNullOrEmpty()

                if (searchQueryText.length > 2) {
                    searchTracks(searchQueryText)
                } else {
                    adapter.updateTracks(emptyList())
                    showDefaultState()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /** Поиск треков */
    private fun searchTracks(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val url = URL("https://itunes.apple.com/search?entity=song&term=$encodedQuery")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val trackList = parseTracks(response)

                    withContext(Dispatchers.Main) {
                        adapter.updateTracks(trackList)
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

    /** Разбор JSON */
    private fun parseTracks(json: String): List<Track> {
        val resultList = mutableListOf<Track>()
        val jsonObject = JSONObject(json)
        val results = jsonObject.getJSONArray("results")

        for (i in 0 until results.length()) {
            val trackObj = results.getJSONObject(i)
            val trackName = trackObj.optString("trackName", "Без названия")
            val artistName = trackObj.optString("artistName", "Неизвестен")
            val trackTimeMillis = trackObj.optLong("trackTimeMillis", 0)
            val artworkUrl = trackObj.optString("artworkUrl100", "")

            resultList.add(Track(trackName, artistName, millisToTime(trackTimeMillis), artworkUrl))
        }
        return resultList
    }

    /** Преобразование миллисекунд в M:SS */
    private fun millisToTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    /** Скрыть клавиатуру */
    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    /** Отображение состояний */
    private fun showResultState(isEmpty: Boolean) {
        recyclerView.isVisible = !isEmpty
        placeholderLayout.isVisible = isEmpty
        networkErrorLayout.isVisible = false
    }

    private fun showNetworkError() {
        recyclerView.isVisible = false
        placeholderLayout.isVisible = false
        networkErrorLayout.isVisible = true
    }

    private fun showDefaultState() {
        recyclerView.isVisible = false
        placeholderLayout.isVisible = false
        networkErrorLayout.isVisible = false
    }
}
