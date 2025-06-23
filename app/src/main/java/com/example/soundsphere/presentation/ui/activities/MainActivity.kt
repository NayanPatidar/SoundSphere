package com.example.soundsphere.presentation.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.soundsphere.R
import com.example.soundsphere.data.models.PlayerState
import com.example.soundsphere.databinding.ActivityMainBinding
import com.example.soundsphere.databinding.LayoutPlayerFullBinding
import com.example.soundsphere.presentation.viewmodel.PlayerViewModel
import com.example.soundsphere.services.MusicPlaybackService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var playerSheetBinding: LayoutPlayerFullBinding
    private val playerViewModel: PlayerViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MotionLayout>

    private var isBottomSheetAnimating = false
    private var isUserSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.parseColor("#121212")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerSheetBinding = binding.playerSheet

        setupNavigation()
        setupBottomSheet()
        setupPlayerControls()
        observePlayerState()
        startPlaybackService()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHostFragment.navController)
    }

    private fun startPlaybackService() {
        val intent = Intent(this, MusicPlaybackService::class.java)
        startService(intent)
    }

    private fun observePlayerState() {
        lifecycleScope.launch {
            playerViewModel.playerState.collect { state ->
                if (state.currentSong == null) {
                    bottomSheetBehavior.isHideable = true
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    bottomSheetBehavior.isHideable = false
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    updatePlayerUI(state)
                }
            }
        }
    }

    private fun setupPlayerControls() {
        playerSheetBinding.playPauseButton.setOnClickListener {
            val currentState = playerViewModel.playerState.value
            if (currentState.currentSong != null) {
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
            playerSheetBinding.songTitleTextView.text = song.name
            playerSheetBinding.songArtistTextView.text = song.subtitle
            Glide.with(this)
                .load(song.imageUrl)
                .placeholder(R.drawable.placeholder_music)
                .into(playerSheetBinding.albumArtImageView)
        }

        val playPauseIcon = if (state.isPlaying) R.drawable.pause else R.drawable.play2
        playerSheetBinding.playPauseButton.setImageResource(playPauseIcon)

        val duration = state.totalDurationMs.toInt()
        val position = state.currentPositionMs.toInt()

        playerSheetBinding.playerSeekBar.max = duration
        playerSheetBinding.totalDurationTextView.text = formatTime(state.totalDurationMs)
        playerSheetBinding.currentTimeTextView.text = formatTime(state.currentPositionMs)
        if (!isUserSeeking) {
            playerSheetBinding.playerSeekBar.progress = position
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun setupBottomSheet() {
        val playerSheet: MotionLayout = playerSheetBinding.playerSheetContainer
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet)

        val miniPlayerContentHeightPx = resources.getDimensionPixelSize(R.dimen.mini_player_height)
        val bottomNavHeightPx = resources.getDimensionPixelSize(R.dimen.bottom_nav_height)

        bottomSheetBehavior.apply {
            peekHeight = miniPlayerContentHeightPx + bottomNavHeightPx
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            isDraggable = false
            isFitToContents = false
            expandedOffset = 0
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        playerSheet.progress = 0f
                        Log.d("PlayerDebug", "Player state: COLLAPSED (forced)")
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        playerSheet.progress = 0f
                        Log.d("PlayerDebug", "Player state: HIDDEN")
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                playerSheet.progress = 0f
                bottomSheet.translationY = -bottomNavHeightPx.toFloat()
            }
        })
        setupPlayerClickListeners(disableExpansion = true)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPlayerClickListeners(disableExpansion: Boolean = false) {
        if (disableExpansion) {
            playerSheetBinding.root.setOnClickListener {
                Log.d("PlayerDebug", "Mini-player clicked. Expansion is disabled.")
            }
            playerSheetBinding.albumArtCardView.setOnClickListener { playerSheetBinding.root.performClick() }
            playerSheetBinding.songTitleTextView.setOnClickListener { playerSheetBinding.root.performClick() }
            playerSheetBinding.songArtistTextView.setOnClickListener { playerSheetBinding.root.performClick() }
        } else {
            playerSheetBinding.root.setOnClickListener {
                Log.d("PlayerDebug", "Mini-player clicked, would expand if enabled.")
            }
        }
    }

    override fun onBackPressed() {
        when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}