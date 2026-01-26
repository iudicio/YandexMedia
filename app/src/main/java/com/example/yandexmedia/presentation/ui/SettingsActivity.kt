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
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val app = application as SettingsApp

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<SwitchMaterial>(R.id.themeSwitcher).apply {
            isChecked = app.darkTheme
            setOnCheckedChangeListener { _, checked ->
                app.switchTheme(checked)
            }
        }

        findViewById<LinearLayout>(R.id.shareApp).setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
                }
            )
        }

        findViewById<LinearLayout>(R.id.support).setOnClickListener {
            try {
                startActivity(
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${getString(R.string.email_to)}"))
                )
            } catch (e: Exception) {
                Toast.makeText(this, R.string.no_email_client, Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<LinearLayout>(R.id.userAgreement).setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer_link)))
            )
        }
    }
}
