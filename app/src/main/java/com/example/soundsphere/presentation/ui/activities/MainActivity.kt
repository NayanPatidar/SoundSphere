// File: presentation/ui/activities/MainActivity.kt
package com.example.soundsphere.presentation.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.soundsphere.R
import com.example.soundsphere.data.models.PlayerState
import com.example.soundsphere.databinding.ActivityMainBinding
import com.example.soundsphere.databinding.ItemNowPlayingBarBinding
import com.example.soundsphere.presentation.viewmodel.PlayerViewModel
import com.example.soundsphere.services.MusicPlaybackService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var playerBarBinding: ItemNowPlayingBarBinding
    private val playerViewModel: PlayerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate() called - START")
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "Setting window colors")
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = Color.parseColor("#121212")

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            playerBarBinding = ItemNowPlayingBarBinding.bind(binding.playerBar.root)
            setupNavigation()
            setupPlayerControls()
            startPlaybackService()
            startPlaybackService()
            observePlayerState()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
            e.printStackTrace()
        }
    }

    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            binding.bottomNavigation.setupWithNavController(navController)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPlaybackService() {
        val intent = Intent(this, MusicPlaybackService::class.java)
        startService(intent)
    }

    private fun observePlayerState() {
        lifecycleScope.launch {
            playerViewModel.playerState.collect { state ->
                if (state.currentSong == null) {
                    playerBarBinding.root.visibility = View.GONE
                } else {
                    playerBarBinding.root.visibility = View.VISIBLE
                    updatePlayerUI(state)
                }
            }
        }
    }

    private fun setupPlayerControls() {
        playerBarBinding.playPauseButton.setOnClickListener {
            val currentState = playerViewModel.playerState.value
            if (currentState?.currentSong != null) {
                if (currentState.isPlaying) {
                    sendCommandToService(MusicPlaybackService.ACTION_PAUSE)
                } else {
                    sendCommandToService(MusicPlaybackService.ACTION_PLAY)
                }
            }
        }
    }

    private fun sendCommandToService(action: String) {
        val intent = Intent(this, MusicPlaybackService::class.java).apply {
            this.action = action
        }
        startService(intent)
    }

    private fun updatePlayerUI(state: PlayerState) {
        state.currentSong?.let { song ->
            playerBarBinding.songTitleTextView.text = song.name
            playerBarBinding.songArtistTextView.text = song.subtitle
            Glide.with(this)
                .load(song.imageUrl)
                .placeholder(R.drawable.placeholder_music)
                .into(playerBarBinding.albumArtImageView)
        }

        val playPauseIcon = if (state.isPlaying) R.drawable.pause else R.drawable.play2
        playerBarBinding.playPauseButton.setImageResource(playPauseIcon)
    }
}
