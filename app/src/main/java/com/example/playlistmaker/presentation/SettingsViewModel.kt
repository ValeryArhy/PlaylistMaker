package com.example.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.R

class SettingsViewModel(private val context: Context) : ViewModel() {

    fun switchTheme(isDark: Boolean) {
        (context.applicationContext as App).switchTheme(isDark)
    }

    fun getShareIntent(): Intent {
        val shareText = context.getString(R.string.shareLink)
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    }

    fun getSupportIntent(): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.mailAgreement)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.themeMail))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.mailSupport))
        }
    }

    fun getAgreementIntent(): Intent {
        val url = context.getString(R.string.AgreementUrl)
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}