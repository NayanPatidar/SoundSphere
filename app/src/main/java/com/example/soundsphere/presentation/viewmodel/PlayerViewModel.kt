package com.example.soundsphere.presentation.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.soundsphere.data.models.MusicItemPlayer
import com.example.soundsphere.data.repository.PlayerStateRepository
import com.example.soundsphere.services.MusicPlaybackService

/**
 * This ViewModel MUST be simple. It acts as a bridge to the Repository.
 * It does NOT manage its own state.
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    // It exposes the state directly from the single source of truth.
    val playerState = PlayerStateRepository.playerState

    // The UI calls this to play a new song.
    fun playNewSong(song: MusicItemPlayer, playlist: List<MusicItemPlayer>) {
        // It tells the Repository about the new song.
        PlayerStateRepository.playSong(song, playlist)
    }

    // The UI calls this to toggle play/pause.
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
