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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MusicPlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    companion object {
        const val ACTION_PLAY = "com.example.soundsphere.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.soundsphere.ACTION_PAUSE"
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                // Report the new playing state to the central repository.
                PlayerStateRepository.updateIsPlaying(isPlaying)
            }
        })
        observePlayerState()
    }

    private fun observePlayerState() {
        serviceScope.launch {
            PlayerStateRepository.playerState
                .map { it.currentSong }
                .distinctUntilChanged()
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
        }
        return super.onStartCommand(intent, flags, startId)
    }

}