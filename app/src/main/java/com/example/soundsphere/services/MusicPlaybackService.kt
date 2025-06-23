package com.example.soundsphere.services

import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.soundsphere.data.repository.PlayerStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    companion object {
        const val ACTION_PLAY = "com.example.soundsphere.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.soundsphere.ACTION_PAUSE"
        const val ACTION_SEEK = "com.example.soundsphere.ACTION_SEEK"
        const val EXTRA_SEEK_POSITION = "com.example.soundsphere.EXTRA_SEEK_POSITION"
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

        // Listen for changes in ExoPlayer's state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // Report the new playing state to the central repository.
                PlayerStateRepository.updateIsPlaying(isPlaying)
            }
        })

        observePlayerStateForPlayback()

        startProgressUpdater()
    }

    private fun observePlayerStateForPlayback() {
        serviceScope.launch {
            // Listen for changes to the current song in the repository
            PlayerStateRepository.playerState
                .map { it.currentSong } // Only care about the song
                .distinctUntilChanged() // Only react when the song actually changes
                .collect { currentSong ->
                    if (currentSong?.songUrl != null) {
                        playSong(currentSong.songUrl)
                    } else {
                        player.stop()
                        player.clearMediaItems()
                    }
                }
        }
    }

    /**
     * This is the key function for your request.
     * It launches a coroutine that runs as long as the service is alive,
     * updating the playback progress every second.
     */
    private fun startProgressUpdater() {
        serviceScope.launch {
            while (isActive) {
                // Only update progress if a song is actually playing
                if (player.isPlaying) {
                    PlayerStateRepository.updatePlaybackPosition(
                        position = player.currentPosition,
                        duration = player.duration
                    )
                }
                // Wait for one second before the next update
                delay(1000)
            }
        }
    }

    private fun playSong(songUrl: String) {
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mediaSession.release()
        serviceJob.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> player.play()
            ACTION_PAUSE -> player.pause()
            ACTION_SEEK -> {
                val seekPosition = intent.getLongExtra(EXTRA_SEEK_POSITION, 0L)
                player.seekTo(seekPosition)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}
