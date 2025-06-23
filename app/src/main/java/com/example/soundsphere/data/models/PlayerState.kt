package com.example.soundsphere.data.models

data class PlayerState(
    val currentSong: MusicItemPlayer? = null,
    val playlist: List<MusicItemPlayer> = emptyList(),
    val currentSongIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val totalDurationMs: Long = 0L
)

data class MusicItemPlayer(
    val id: String,
    val name: String,
    val type: String,
    val subtitle: String,
    val imageUrl: String?,
    val songUrl: String?,
    val duration: Int = 0
)
