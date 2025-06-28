package com.example.soundsphere.presentation.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.soundsphere.data.models.MusicItemPlayer
import com.example.soundsphere.data.repository.PlayerStateRepository
import com.example.soundsphere.services.MusicPlaybackService

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    val playerState = PlayerStateRepository.playerState
    fun playNewSong(song: MusicItemPlayer, playlist: List<MusicItemPlayer>) {
        PlayerStateRepository.playSong(song, playlist)
    }

    fun togglePlayPause() {
        val intent = Intent(getApplication(), MusicPlaybackService::class.java).apply {
            action = if (playerState.value.isPlaying) {
                MusicPlaybackService.ACTION_PAUSE
            } else {
                MusicPlaybackService.ACTION_PLAY
            }
        }
        getApplication<Application>().startService(intent)
    }

    fun seekToPosition(position: Long) {
        val intent = Intent(getApplication(), MusicPlaybackService::class.java).apply {
            action = MusicPlaybackService.ACTION_SEEK
            putExtra(MusicPlaybackService.EXTRA_SEEK_POSITION, position)
        }
        getApplication<Application>().startService(intent)
    }

}
