package com.example.yandexmedia.presentation.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupThemeSwitcher(view)
        setupClicks(view)
    }

    private fun setupThemeSwitcher(view: View) {
        view.findViewById<SwitchMaterial>(R.id.themeSwitcher).apply {
            isChecked = viewModel.isDarkTheme()
            setOnCheckedChangeListener { _, checked ->
                viewModel.onThemeChanged(checked)
            }
        }
    }

    private fun setupClicks(view: View) {
        view.findViewById<View>(R.id.shareApp).setOnClickListener { onShareClicked() }
        view.findViewById<View>(R.id.support).setOnClickListener { onSupportClicked() }
        view.findViewById<View>(R.id.userAgreement).setOnClickListener { onUserAgreementClicked() }
    }

    private fun onShareClicked() {
        val ok = viewModel.share(
            activity = requireActivity(),
            text = getString(R.string.share_message),
            chooserTitle = getString(R.string.share_chooser_title)
        )
        if (!ok) showToast(R.string.share_error)
    }

    private fun onSupportClicked() {
        val ok = viewModel.email(
            activity = requireActivity(),
            to = getString(R.string.support_email),
            subject = getString(R.string.support_subject),
            body = getString(R.string.support_body)
        )
        if (!ok) showToast(R.string.no_email_app)
    }

    private fun onUserAgreementClicked() {
        val ok = viewModel.openUrl(
            activity = requireActivity(),
            url = getString(R.string.user_agreement_url)
        )
        if (!ok) showToast(R.string.no_browser_app)
    }

    private fun showToast(messageRes: Int) {
        Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
    }
}