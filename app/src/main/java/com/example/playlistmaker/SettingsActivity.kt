package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)



        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val shareButton = findViewById<MaterialTextView>(R.id.shareButton)
        val supportButton = findViewById<MaterialTextView>(R.id.supportButton)
        val agreementButton = findViewById<MaterialTextView>(R.id.agreementButton)

        shareButton.setOnClickListener {
            shareAppLink()
        }
        supportButton.setOnClickListener {
            openSupport()
        }

        agreementButton.setOnClickListener {
            openAgreement()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun shareAppLink() {
        val shareLink = getString(R.string.shareLink)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareLink)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.shareLink)))
    }

    private fun openSupport() {
        val mailAgreement = getString(R.string.mailAgreement)
        val themeMail = getString(R.string.themeMail)
        val mail = getString(R.string.mailSupport)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mailAgreement))
            putExtra(Intent.EXTRA_SUBJECT, themeMail)
            putExtra(Intent.EXTRA_TEXT, mail)
        }
        startActivity(intent)
    }

    private fun openAgreement() {
        val url = getString(R.string.AgreementUrl)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }
}
