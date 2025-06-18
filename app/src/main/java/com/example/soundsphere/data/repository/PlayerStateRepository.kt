package com.example.soundsphere.data.repository

import com.example.soundsphere.data.models.MusicItemPlayer
import com.example.soundsphere.data.models.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PlayerStateRepository {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    fun updateState(newState: PlayerState) {
        _playerState.value = newState
    }

    fun playSong(song: MusicItemPlayer, playlist: List<MusicItemPlayer>) {
        val songIndex = playlist.indexOf(song)
        _playerState.value = PlayerState(
            currentSong = song,
            playlist = playlist,
            currentSongIndex = songIndex,
            isPlaying = true
        )
    }

    fun updatePlaybackPosition(position: Long, duration: Long) {
        _playerState.value = _playerState.value.copy(
            currentPositionMs = position,
            totalDurationMs = duration
        )
    }

    fun hidePlayer() {
        _playerState.value = PlayerState()
    }

    fun updateIsPlaying(isPlaying: Boolean) {
        if (_playerState.value.currentSong != null) {
            _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
        }
    }
}