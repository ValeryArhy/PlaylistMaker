package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
            applyTheme(isChecked)
        }

        binding.shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.shareLink))
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        binding.supportButton.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mailAgreement)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.themeMail))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.mailSupport))
            }
            startActivity(mailIntent)
        }

        binding.agreementButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.AgreementUrl)))
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.isDarkTheme.observe(this) { isDark ->
            binding.themeSwitcher.isChecked = isDark
        }
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}