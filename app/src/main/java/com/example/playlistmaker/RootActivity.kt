package com.example.playlistmaker

import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Привязка BottomNavigationView
        binding.bottomNavigationView.setupWithNavController(navController)

        // Слушаем изменения destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Скрываем BottomNavigationView только на PlayerFragment
            binding.bottomNavigationView.isVisible = destination.id != R.id.playerFragment
        }
    }
}