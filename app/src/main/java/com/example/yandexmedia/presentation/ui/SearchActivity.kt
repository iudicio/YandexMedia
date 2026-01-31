package com.example.yandexmedia.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexmedia.R
import com.example.yandexmedia.creator.InteractorCreator
import com.example.yandexmedia.domain.interactor.SearchHistoryInteractor
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.adapter.TrackAdapter
import com.example.yandexmedia.presentation.viewmodel.SearchState
import com.example.yandexmedia.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var searchRecyclerView: RecyclerView

    private lateinit var placeholderLayout: LinearLayout
    private lateinit var networkErrorLayout: LinearLayout
    private lateinit var retryButton: Button

    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar

    private var searchQueryText: String = ""
    private var isClickAllowed = true

    private val viewModel: SearchViewModel by viewModels {
        InteractorCreator.provideSearchViewModelFactory()
    }


    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistoryInteractor = InteractorCreator.provideSearchHistoryInteractor(this)

        initViews()
        initHistory()
        initSearchList()
        initListeners()
        observeState()

        searchEditText.requestFocus()
        showHistoryIfNeeded()
    }

    private fun initViews() {
        val backButton = findViewById<ImageView>(R.id.backButton)
        progressBar = findViewById(R.id.progressBar)

        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearButton)

        searchRecyclerView = findViewById(R.id.searchResultsRecyclerView)

        placeholderLayout = findViewById(R.id.placeholderLayout)
        networkErrorLayout = findViewById(R.id.networkErrorLayout)
        retryButton = findViewById(R.id.retryButton)

        historyContainer = findViewById(R.id.historyContainer)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        backButton.setOnClickListener { finish() }
    }

    private fun initHistory() {
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = TrackAdapter(
            arrayListOf(),
            onTrackClick = { track ->
                if (clickDebounce()) openPlayer(track)
            },
            onClearHistoryClick = {
                searchHistoryInteractor.clear()
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
                if (clickDebounce()) {
                    searchHistoryInteractor.addTrack(track)
                    openPlayer(track)
                }
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
            viewModel.onQueryChanged("") // сбрасываем состояние поиска
            showHistoryIfNeeded()
        }

        retryButton.setOnClickListener {
            if (searchQueryText.length > 2) {
                viewModel.onRetry()
            } else {
                showHistoryIfNeeded()
            }
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
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

                viewModel.onQueryChanged(searchQueryText)

                if (searchQueryText.length > 2) {
                    historyContainer.isVisible = false
                } else {
                    searchAdapter.updateTracks(emptyList())
                    if (searchEditText.hasFocus()) showHistoryIfNeeded()
                    else showDefaultState()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    SearchState.Idle -> {
                        hideLoading()
                        if (searchEditText.text.isNullOrEmpty() && searchEditText.hasFocus()) {
                            showHistoryIfNeeded()
                        } else {
                            showDefaultState()
                        }
                    }

                    SearchState.Loading -> showLoading()

                    SearchState.Empty -> {
                        historyContainer.isVisible = false
                        searchAdapter.updateTracks(emptyList())
                        showResultState(isEmpty = true)
                    }

                    is SearchState.Content -> {
                        historyContainer.isVisible = false
                        searchAdapter.updateTracks(state.tracks)
                        showResultState(isEmpty = false)
                    }

                    SearchState.NetworkError -> showNetworkError()
                }
            }
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(EXTRA_TRACK, track)
        startActivity(intent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (current) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun showHistoryIfNeeded() {
        val history = searchHistoryInteractor.getHistory()
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

    private fun showLoading() {
        progressBar.isVisible = true
        searchRecyclerView.isVisible = false
        placeholderLayout.isVisible = false
        networkErrorLayout.isVisible = false
        historyContainer.isVisible = false
    }

    private fun hideLoading() {
        progressBar.isVisible = false
    }

    private fun showResultState(isEmpty: Boolean) {
        hideLoading()
        searchRecyclerView.isVisible = !isEmpty
        placeholderLayout.isVisible = isEmpty
        networkErrorLayout.isVisible = false
        historyContainer.isVisible = false
    }

    private fun showNetworkError() {
        hideLoading()
        searchRecyclerView.isVisible = false
        placeholderLayout.isVisible = false
        networkErrorLayout.isVisible = true
        historyContainer.isVisible = false
    }

    private fun showDefaultState() {
        hideLoading()
        searchRecyclerView.isVisible = false
        placeholderLayout.isVisible = false
        networkErrorLayout.isVisible = false
        historyContainer.isVisible = false
    }

    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}
