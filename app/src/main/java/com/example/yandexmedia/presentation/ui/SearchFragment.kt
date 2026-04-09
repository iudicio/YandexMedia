package com.example.yandexmedia.presentation.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.adapter.TrackAdapter
import com.example.yandexmedia.presentation.viewmodel.SearchState
import com.example.yandexmedia.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var searchAdapter: TrackAdapter
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var networkErrorLayout: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar

    private var searchQueryText: String = ""
    private var isClickAllowed = true
    private var clickJob: Job? = null

    private val viewModel: SearchViewModel by viewModel()

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initHistory()
        initSearchList()
        initListeners()
        observeState()

        searchEditText.requestFocus()
        showHistoryIfNeeded()
    }

    override fun onDestroyView() {
        clickJob?.cancel()
        clickJob = null
        isClickAllowed = true
        super.onDestroyView()
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        searchEditText = view.findViewById(R.id.searchEditText)
        clearButton = view.findViewById(R.id.clearButton)
        searchRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        placeholderLayout = view.findViewById(R.id.placeholderLayout)
        networkErrorLayout = view.findViewById(R.id.networkErrorLayout)
        retryButton = view.findViewById(R.id.retryButton)
        historyContainer = view.findViewById(R.id.historyContainer)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
    }

    private fun initHistory() {
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        historyAdapter = TrackAdapter(
            arrayListOf(),
            onTrackClick = { track ->
                if (clickDebounce()) openPlayer(track)
            },
            onClearHistoryClick = {
                viewModel.clearHistory()
                historyAdapter.updateTracks(emptyList())
                historyContainer.isVisible = false
            },
            showFooter = true
        )

        historyRecyclerView.adapter = historyAdapter
    }

    private fun initSearchList() {
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchAdapter = TrackAdapter(
            arrayListOf(),
            onTrackClick = { track ->
                if (clickDebounce()) {
                    viewModel.addToHistory(track)
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
            viewModel.onQueryChanged("")
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQueryText = s?.toString() ?: ""
                clearButton.isVisible = !s.isNullOrEmpty()

                viewModel.onQueryChanged(searchQueryText)

                if (searchQueryText.length > 2) {
                    historyContainer.isVisible = false
                } else {
                    searchAdapter.updateTracks(emptyList())
                    if (searchEditText.hasFocus()) showHistoryIfNeeded() else showDefaultState()
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.history.collect { history ->
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
        }
    }

    private fun openPlayer(track: Track) {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.searchFragment) return

        val bundle = Bundle().apply {
            putParcelable("track", track)
        }
        navController.navigate(R.id.playerFragment, bundle)
    }

    private var lastClickTime = 0L

    private fun clickDebounce(): Boolean {
        if (!isClickAllowed) return false

        isClickAllowed = false
        clickJob?.cancel()
        clickJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            isClickAllowed = true
        }

        return true
    }

    private fun showHistoryIfNeeded() {
        viewModel.loadHistory()
        val history = viewModel.history.value

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
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}