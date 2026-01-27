package com.example.yandexmedia.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yandexmedia.R
import com.example.yandexmedia.SettingsApp
import com.example.yandexmedia.domain.interactor.ThemeInteractor
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.yandexmedia.creator.InteractorCreator

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeInteractor = InteractorCreator.provideThemeInteractor(this)

        findViewById<SwitchMaterial>(R.id.themeSwitcher).apply {
            isChecked = themeInteractor.isDarkTheme()
            setOnCheckedChangeListener { _, checked ->
                themeInteractor.setDarkTheme(checked)
                (application as SettingsApp).applyTheme()
            }
        }
    }
}
