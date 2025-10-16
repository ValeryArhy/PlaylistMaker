package com.example.playlistmaker.player.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()
    private var coverPath: String? = null

    private val addPicture =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                coverPath = saveImageToPrivateStorage(uri)
                coverPath?.let { binding.addPhotoPlaceholder.setImageURI(Uri.fromFile(File(it))) }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
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
            findNavController().popBackStack()
        }

        binding.savePlaylist.setOnClickListener {
            val name = binding.addName.text.toString().trim()
            if (name.isNotEmpty()) {
                val description = binding.description.text.toString().trim()
                viewModel.savePlaylist(name, description, coverPath) {
                    findNavController().popBackStack()
                }
            } else {
                binding.description.error = "Введите название плейлиста"
            }
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
