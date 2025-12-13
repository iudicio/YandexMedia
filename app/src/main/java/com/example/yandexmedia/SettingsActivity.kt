package com.example.yandexmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val darkSwitch = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val shareApp = findViewById<LinearLayout>(R.id.shareApp)
        val support = findViewById<LinearLayout>(R.id.support)
        val userAgreement = findViewById<LinearLayout>(R.id.userAgreement)

        val app = application as Appp

        darkSwitch.isChecked = app.darkTheme

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        darkSwitch.setOnCheckedChangeListener { _, checked ->
            app.switchTheme(checked)
        }

        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.share_message, getString(R.string.course_link))
                )
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        }

        support.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_to)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            }

            try {
                startActivity(
                    Intent.createChooser(
                        emailIntent,
                        getString(R.string.email_chooser_title)
                    )
                )
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    getString(R.string.no_email_client),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        userAgreement.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.offer_link))
            }
            startActivity(browserIntent)
        }
    }
}
