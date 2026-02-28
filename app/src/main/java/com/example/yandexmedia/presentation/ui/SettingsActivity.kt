package com.example.yandexmedia.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yandexmedia.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.yandexmedia.presentation.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupThemeSwitcher()
        setupClicks()
    }

    private fun setupThemeSwitcher() {
        findViewById<SwitchMaterial>(R.id.themeSwitcher).apply {
            isChecked = viewModel.isDarkTheme()
            setOnCheckedChangeListener { _, checked ->
                viewModel.onThemeChanged(checked)
            }
        }
    }

    private fun setupClicks() {
        findViewById<View>(R.id.backButton).setOnClickListener { finish() }

        findViewById<View>(R.id.shareApp).setOnClickListener { onShareClicked() }
        findViewById<View>(R.id.support).setOnClickListener { onSupportClicked() }
        findViewById<View>(R.id.userAgreement).setOnClickListener { onUserAgreementClicked() }
    }

    private fun onShareClicked() {
        val ok = viewModel.share(
            activity = this,
            text = getString(R.string.share_message),
            chooserTitle = getString(R.string.share_chooser_title)
        )
        if (!ok) showToast(R.string.share_error)
    }

    private fun onSupportClicked() {
        val ok = viewModel.email(
            activity = this,
            to = getString(R.string.support_email),
            subject = getString(R.string.support_subject),
            body = getString(R.string.support_body)
        )
        if (!ok) showToast(R.string.no_email_app)
    }

    private fun onUserAgreementClicked() {
        val ok = viewModel.openUrl(
            activity = this,
            url = getString(R.string.user_agreement_url)
        )
        if (!ok) showToast(R.string.no_browser_app)
    }

    private fun showToast(messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }
}
