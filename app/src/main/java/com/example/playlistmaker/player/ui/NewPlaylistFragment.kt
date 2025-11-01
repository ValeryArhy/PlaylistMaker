package com.example.playlistmaker.player.ui


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistFragment : Fragment() {

    protected var _binding: FragmentNewPlaylistBinding? = null
    protected val binding get() = _binding!!

    protected open val  viewModel: NewPlaylistViewModel by viewModel()
    protected var coverPath: String? = null

    private val addPicture =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                coverPath = saveImageToPrivateStorage(uri)
                coverPath?.let { loadImage(Uri.fromFile(File(it))) }
            }
        }

    protected fun loadImage(uri: Uri) {
        binding.addPhotoPlaceholderIcon.isVisible=false
        binding.addPhotoImage.isVisible=true

        Glide.with(binding.addPhotoImage.context)
            .load(uri)
            .into(binding.addPhotoImage)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPhotoPlaceholder.setOnClickListener {
            addPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.toolbar.setNavigationOnClickListener {
            handleExit()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            handleExit()
        }

        binding.savePlaylist.setOnClickListener {
            val name = binding.addName.text.toString().trim()
            if (name.isNotEmpty()) {
                val description = binding.description.text.toString().trim()
                viewModel.savePlaylist(name, description, coverPath) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.playlist_created, name),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        }

        binding.addName.doOnTextChanged { text, _, _, _ ->
            val isNotEmpty = !text.isNullOrBlank()
            binding.savePlaylist.isEnabled = isNotEmpty
            binding.addName.error = null
        }
    }

    private fun handleExit() {
        val hasData = !binding.addName.text.isNullOrBlank() ||
                !binding.description.text.isNullOrBlank() ||
                coverPath != null

        if (hasData) {
            MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
                .setTitle(getString(R.string.dialog_finish_playlist_title))
                .setMessage(getString(R.string.dialog_finish_playlist_message))
                .setNegativeButton(getString(R.string.dialog_finish_playlist_cancel), null)
                .setPositiveButton(getString(R.string.dialog_finish_playlist_confirm)) { _, _ ->
                    findNavController().popBackStack()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream ?: return null

            val dir = File(requireContext().filesDir, "playlist_covers")
            if (!dir.exists()) dir.mkdirs()

            val fileName = "cover_${System.currentTimeMillis()}.jpg"
            val file = File(dir, fileName)
            val outputStream = FileOutputStream(file)

            BitmapFactory.decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            outputStream.close()
            inputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
