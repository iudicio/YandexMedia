package com.example.yandexmedia.presentation.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.model.Track
import com.example.yandexmedia.presentation.theme.YandexDisplay
import com.example.yandexmedia.presentation.ui.components.TrackRow
import com.example.yandexmedia.presentation.viewmodel.SearchState

@Composable
fun SearchScreen(
    query: String,
    state: SearchState,
    history: List<Track>,
    onQueryChange: (String) -> Unit,
    onTrackClick: (Track) -> Unit,
    onHistoryTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit,
    onRetry: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearchFocused by remember { mutableStateOf(false) }
    var showHistoryWithoutFocus by remember { mutableStateOf(false) }
    val backgroundColor = colorResource(R.color.color_back_ground)
    val textColor = colorResource(R.color.color_black)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    BackHandler(enabled = isSearchFocused) {
        showHistoryWithoutFocus = false
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Text(
            text = stringResource(R.string.title_search),
            color = textColor,
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp)
        )
        SearchField(
            query = query,
            onQueryChange = {
                if (it.isNotEmpty()) showHistoryWithoutFocus = false
                onQueryChange(it)
            },
            onClear = {
                showHistoryWithoutFocus = true
                onQueryChange("")
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            onFocusChanged = { isSearchFocused = it },
            focusRequester = focusRequester
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                SearchState.Loading -> CircularProgressIndicator(
                    color = colorResource(R.color.color_progressbar),
                    modifier = Modifier.size(44.dp)
                )
                SearchState.NetworkError -> SearchMessage(
                    imageRes = R.drawable.not_connection,
                    title = stringResource(R.string.network_error),
                    subtitle = stringResource(R.string.network_error2),
                    action = {
                        AppButton(
                            label = stringResource(R.string.update),
                            onClick = onRetry,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                )
                SearchState.Empty -> SearchMessage(
                    imageRes = R.drawable.ic_logo_media,
                    title = stringResource(R.string.nothing_found)
                )
                is SearchState.Content -> TrackList(state.tracks, onTrackClick)
                SearchState.Idle -> if (
                    query.isEmpty() &&
                    history.isNotEmpty() &&
                    (isSearchFocused || showHistoryWithoutFocus)
                ) {
                    SearchHistory(history, onHistoryTrackClick, onClearHistory)
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    focusRequester: FocusRequester
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = TextStyle(
            color = Color.Black,
            fontFamily = YandexDisplay,
            fontSize = 16.sp
        ),
        cursorBrush = SolidColor(Color.Black),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
        }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
            .height(48.dp)
            .background(colorResource(R.color.color_gray_light), RoundedCornerShape(8.dp))
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search_search),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.hint_search),
                            color = colorResource(R.color.color_gray_dark),
                            fontFamily = YandexDisplay,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clear),
                        contentDescription = stringResource(R.string.clear_search),
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = onClear)
                    )
                }
            }
        }
    )
}

@Composable
private fun SearchHistory(
    history: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.history),
            color = colorResource(R.color.color_black),
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 16.dp)
        ) {
            items(history, key = { it.trackId }) { track ->
                TrackRow(track = track, onClick = { onTrackClick(track) })
            }
            item {
                Box(
                    Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppButton(stringResource(R.string.clear_history), onClearHistory)
                }
            }
        }
    }
}

@Composable
private fun TrackList(tracks: List<Track>, onClick: (Track) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(tracks, key = { it.trackId }) { track ->
            TrackRow(track = track, onClick = { onClick(track) })
        }
    }
}

@Composable
private fun SearchMessage(
    imageRes: Int,
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = title,
            color = colorResource(R.color.color_black),
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = if (subtitle == null) 16.sp else 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        subtitle?.let {
            Text(
                text = it,
                color = colorResource(R.color.color_black),
                fontFamily = YandexDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        action?.let { Box(Modifier.padding(top = 24.dp)) { it() } }
    }
}

@Composable
private fun AppButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(colorResource(R.color.btn_update), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = colorResource(R.color.btn_update_text),
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}
