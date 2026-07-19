package com.example.yandexmedia.presentation.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.theme.YandexDisplay

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onShare: () -> Unit,
    onSupport: () -> Unit,
    onAgreement: () -> Unit
) {
    val background = colorResource(R.color.color_back_ground)
    val textColor = colorResource(R.color.color_black)
    val switchAccent = colorResource(R.color.color_accent)
    Column(
        Modifier
            .fillMaxSize()
            .background(background)
            .padding(top = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.title_settings),
            color = textColor,
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 16.dp)
        )
        SettingsRow(label = stringResource(R.string.label_dark_theme)) {
            Switch(
                checked = darkTheme,
                onCheckedChange = onThemeChanged,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = switchAccent.copy(alpha = 0.54f),
                    checkedThumbColor = switchAccent,
                    uncheckedTrackColor = colorResource(R.color.color_gray_light),
                    uncheckedThumbColor = colorResource(R.color.color_gray_dark),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
        SettingsActionRow(R.string.label_share_app, R.drawable.ic_send, onShare)
        SettingsActionRow(R.string.label_support, R.drawable.ic_help, onSupport)
        SettingsActionRow(R.string.label_user_agreement, R.drawable.ic_arrow_right, onAgreement)
    }
}

@Composable
private fun SettingsActionRow(
    labelRes: Int,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    val label = stringResource(labelRes)
    SettingsRow(label = label, onClick = onClick) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colorResource(R.color.color_black),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsRow(
    label: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    val clickableModifier = if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = colorResource(R.color.color_black),
            fontFamily = YandexDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}
