package com.example.yandexmedia.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.yandexmedia.R
import com.example.yandexmedia.presentation.theme.YandexMediaTheme
import com.example.yandexmedia.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        ComposeView(requireContext()).apply {
            id = R.id.compose_view_settings
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                YandexMediaTheme(darkTheme = viewModel.isDarkTheme()) {
                    SettingsScreen(
                        darkTheme = viewModel.isDarkTheme(),
                        onThemeChanged = viewModel::onThemeChanged,
                        onShare = ::onShareClicked,
                        onSupport = ::onSupportClicked,
                        onAgreement = ::onUserAgreementClicked
                    )
                }
            }
        }

    private fun onShareClicked() {
        if (!viewModel.share(requireActivity(), getString(R.string.share_message), getString(R.string.share_chooser_title))) showToast(R.string.share_error)
    }

    private fun onSupportClicked() {
        if (!viewModel.email(requireActivity(), getString(R.string.support_email), getString(R.string.support_subject), getString(R.string.support_body))) showToast(R.string.no_email_app)
    }

    private fun onUserAgreementClicked() {
        if (!viewModel.openUrl(requireActivity(), getString(R.string.user_agreement_url))) showToast(R.string.no_browser_app)
    }

    private fun showToast(message: Int) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
