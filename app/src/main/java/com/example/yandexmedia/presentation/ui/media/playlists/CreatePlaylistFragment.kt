package com.example.yandexmedia.presentation.ui.media.playlists

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.yandexmedia.R
import com.example.yandexmedia.domain.interactor.ThemeInteractor
import com.example.yandexmedia.presentation.ui.media.viewmodel.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment(R.layout.fragment_create_playlist) {

    private var selectedCoverUri: Uri? = null

    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private val themeInteractor: ThemeInteractor by inject()

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedCoverUri = uri
                view?.findViewById<ImageView>(R.id.coverImage)?.setImageURI(uri)
                view?.findViewById<ImageView>(R.id.placeholderImage)?.isVisible = false
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)

        val nameInputLayout = view.findViewById<TextInputLayout>(R.id.nameInputLayout)
        val descriptionInputLayout = view.findViewById<TextInputLayout>(R.id.descriptionInputLayout)

        val createButton = view.findViewById<Button>(R.id.createButton)
        val coverContainer = view.findViewById<FrameLayout>(R.id.coverContainer)

        setupInputColors(
            nameInputLayout = nameInputLayout,
            descriptionInputLayout = descriptionInputLayout
        )

        fun updateButtonState() {
            val isNameFilled = nameEditText.text.toString().isNotBlank()
            createButton.isEnabled = isNameFilled

            val buttonColor = if (isNameFilled) {
                R.color.color_primary_permomently
            } else {
                R.color.color_playlist
            }

            ViewCompat.setBackgroundTintList(
                createButton,
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), buttonColor)
                )
            )
        }

        updateButtonState()

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        coverContainer.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        createButton.setOnClickListener {
            onCreateClicked()
        }

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onCloseClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onCloseClicked()
                }
            }
        )
    }

    private fun setupInputColors(
            nameInputLayout: TextInputLayout,
            descriptionInputLayout: TextInputLayout
        ) {
            val textColor = ContextCompat.getColor(
                requireContext(),
                if (themeInteractor.isDarkTheme()) {
                    R.color.white
                } else {
                    R.color.color_black
                }
            )

            val labelColor = ContextCompat.getColor(
                requireContext(),
                if (themeInteractor.isDarkTheme()) {
                    R.color.white
                } else {
                    R.color.color_playlist
                }
            )

            val labelColorStateList = ColorStateList.valueOf(labelColor)

            nameInputLayout.setBoxStrokeColor(textColor)
            descriptionInputLayout.setBoxStrokeColor(textColor)

            nameInputLayout.hintTextColor = labelColorStateList
            descriptionInputLayout.hintTextColor = labelColorStateList

            nameEditText.setHintTextColor(labelColorStateList)
            descriptionEditText.setHintTextColor(labelColorStateList)

            nameEditText.setTextColor(textColor)
            descriptionEditText.setTextColor(textColor)
        }

    private fun onCreateClicked() {
        val playlistName = nameEditText.text.toString().trim()
        val playlistDescription = descriptionEditText.text.toString().trim()

        if (playlistName.isBlank()) return

        val savedCoverPath = selectedCoverUri?.let { uri ->
            saveCoverToPrivateStorage(uri)
        }

        viewModel.createPlaylist(
            name = playlistName,
            description = playlistDescription,
            coverPath = savedCoverPath
        ) {
            Toast.makeText(
                requireContext(),
                "Плейлист $playlistName создан",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigateUp()
        }
    }

    private fun saveCoverToPrivateStorage(uri: Uri): String {
        val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, fileName)

        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file.absolutePath
    }

    private fun onCloseClicked() {
        if (hasUnsavedData()) {
            showConfirmCloseDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun hasUnsavedData(): Boolean {
        return selectedCoverUri != null ||
                nameEditText.text.toString().isNotBlank() ||
                descriptionEditText.text.toString().isNotBlank()
    }

    private fun showConfirmCloseDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }
}