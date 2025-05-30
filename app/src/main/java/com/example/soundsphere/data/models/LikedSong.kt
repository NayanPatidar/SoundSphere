// File: data/models/LikedSong.kt
package com.example.soundsphere.data.models

data class LikedSong(
    val songId: String,
    val songArtistPrimary: String,
    val songArtistSecondary: String,
    val songName: String,
    val songImage: String
)
