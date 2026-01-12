package com.example.playlistmaker

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.databinding.ActivityRootBinding
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    private val viewModel: SettingsViewModel by viewModel()

    private val hiddenMenuDestinations = setOf(
        R.id.playerFragment,
        R.id.newPlaylistFragment,
        R.id.playlistFragment,
        R.id.editPlaylistFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        observeTheme()

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible = destination.id !in hiddenMenuDestinations
        }
    }

    private fun observeTheme() {
        viewModel.isDarkTheme.observe(this) { isDark ->
            val targetMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO


            if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
                AppCompatDelegate.setDefaultNightMode(targetMode)
            }
        }
    }
}