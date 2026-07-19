package com.example.yandexmedia.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import com.example.yandexmedia.R

@Composable
fun YandexMediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val background = colorResource(R.color.color_back_ground)
    val contentColor = colorResource(R.color.color_black)
    val accent = colorResource(R.color.color_primary_permomently)
    val muted = colorResource(R.color.color_track_gray)
    val input = colorResource(R.color.color_gray_light)
    val onPrimary = colorResource(R.color.color_primary_white)
    val colors = remember(darkTheme, background, contentColor, accent, muted, input, onPrimary) {
        if (darkTheme) {
            darkColorScheme(
                primary = accent,
                onPrimary = onPrimary,
                background = background,
                onBackground = contentColor,
                surface = background,
                onSurface = contentColor,
                surfaceVariant = input,
                onSurfaceVariant = muted,
                outline = muted
            )
        } else {
            lightColorScheme(
                primary = accent,
                onPrimary = onPrimary,
                background = background,
                onBackground = contentColor,
                surface = background,
                onSurface = contentColor,
                surfaceVariant = input,
                onSurfaceVariant = muted,
                outline = muted
            )
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
