package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.themeSwitcher.isChecked = (application as App).darkTheme
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
            recreate()
        }

        binding.shareButton.setOnClickListener {
            val shareText = getString(R.string.shareLink)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
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
            val agreementUrl = getString(R.string.AgreementUrl)
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
            startActivity(agreementIntent)
        }
    }
}